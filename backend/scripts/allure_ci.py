#!/usr/bin/env python3
"""Build isolated Allure 2 Pages reports and summarize Maven test results."""

import argparse
import html
import os
import shutil
import subprocess
import xml.etree.ElementTree as ET
from pathlib import Path

SUITES = ("api", "ui", "kafka")
TITLES = {"api": "API", "ui": "UI", "kafka": "Kafka"}


def test_counts(report_dir: Path) -> tuple[int, int, int, int]:
    totals = [0, 0, 0, 0]
    for report in report_dir.glob("TEST-*.xml"):
        try:
            root = ET.parse(report).getroot()
        except ET.ParseError:
            continue
        suites = [root] if root.tag == "testsuite" else root.findall(".//testsuite")
        for suite in suites:
            for index, field in enumerate(("tests", "failures", "errors", "skipped")):
                totals[index] += int(suite.get(field, "0"))
    return tuple(totals)


def job_summary(suite: str, report_dir: Path) -> None:
    tests, failures, errors, skipped = test_counts(report_dir)
    content = (
        f"### {TITLES[suite]} tests\n\n"
        f"Maven reports: {tests} tests, {failures} failures, {errors} errors, "
        f"{skipped} skipped.\n\n"
        "Raw Allure results and Maven XML reports are attached to this workflow run.\n"
    )
    with open(os.environ["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") as summary:
        summary.write(content)


def write_landing(site: Path) -> None:
    entries = []
    for suite in SUITES:
        title = TITLES[suite]
        if (site / suite / "index.html").is_file():
            entries.append(f'<li><a href="{suite}/">{html.escape(title)} tests</a></li>')
        else:
            entries.append(f"<li>{html.escape(title)} tests — отчет пока не создан</li>")
    page = """<!doctype html>
<html lang="ru">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>QA News Service — Allure Reports</title>
  <style>
    body {font: 18px/1.5 system-ui, sans-serif; max-width: 760px; margin: 3rem auto; padding: 0 1rem; color: #172033}
    a {color: #1457a8} li {margin: .7rem 0}
  </style>
</head>
<body>
  <h1>QA News Service — Allure Reports</h1>
  <p>Отчеты API, UI и Kafka обновляются независимо после запуска соответствующих тестов.</p>
  <ul>
""" + "\n".join(f"    {entry}" for entry in entries) + """
  </ul>
</body>
</html>
"""
    (site / "index.html").write_text(page, encoding="utf-8")


def build_site(run_results: Path, site: Path) -> None:
    site.mkdir(parents=True, exist_ok=True)
    updated = []
    for suite in SUITES:
        results = run_results / f"suite-{suite}" / "allure-results"
        if not results.is_dir() or not any(results.glob("*-result.json")):
            continue
        previous_history = site / suite / "history"
        if previous_history.is_dir():
            target_history = results / "history"
            if target_history.exists():
                shutil.rmtree(target_history)
            shutil.copytree(previous_history, target_history)
        subprocess.run(
            ["allure", "generate", str(results), "--clean", "--output", str(site / suite)],
            check=True,
        )
        updated.append(suite)
    write_landing(site)
    with open(os.environ["GITHUB_OUTPUT"], "a", encoding="utf-8") as output:
        output.write(f"updated={'true' if updated else 'false'}\n")
        output.write(f"suites={','.join(updated)}\n")


def report_summary(site: Path, page_url: str, job_results: dict[str, str], updated: str) -> None:
    lines = ["### Allure Report", "", "| Suite | Test job | Maven tests | Report |", "| --- | --- | --- | --- |"]
    for suite in SUITES:
        report_dir = "failsafe-reports" if suite == "kafka" else "surefire-reports"
        tests, failures, errors, skipped = test_counts(Path(os.environ["RUN_RESULTS"]) / f"suite-{suite}" / report_dir)
        counts = f"{tests} tests, {failures} failed, {errors} errors, {skipped} skipped" if tests else "No XML reports"
        report_exists = (site / suite / "index.html").is_file()
        link = f"[Open {TITLES[suite]}]({page_url.rstrip('/')}/{suite}/)" if page_url and report_exists else "Not published"
        lines.append(f"| {TITLES[suite]} | {job_results[suite]} | {counts} | {link} |")
    lines.extend(["", f"Reports generated in this run: {updated or 'none'}.", ""])
    with open(os.environ["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") as summary:
        summary.write("\n".join(lines))


def main() -> None:
    parser = argparse.ArgumentParser()
    subparsers = parser.add_subparsers(dest="command", required=True)
    job = subparsers.add_parser("job-summary")
    job.add_argument("suite", choices=SUITES)
    job.add_argument("reports", type=Path)
    build = subparsers.add_parser("build-site")
    build.add_argument("run_results", type=Path)
    build.add_argument("site", type=Path)
    report = subparsers.add_parser("report-summary")
    report.add_argument("site", type=Path)
    report.add_argument("page_url")
    report.add_argument("api_result")
    report.add_argument("ui_result")
    report.add_argument("kafka_result")
    report.add_argument("updated")
    args = parser.parse_args()
    if args.command == "job-summary":
        job_summary(args.suite, args.reports)
    elif args.command == "build-site":
        build_site(args.run_results, args.site)
    else:
        report_summary(args.site, args.page_url,
                       dict(zip(SUITES, (args.api_result, args.ui_result, args.kafka_result))),
                       args.updated)


if __name__ == "__main__":
    main()
