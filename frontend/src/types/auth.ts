export type UserRole = 'ADMIN' | 'VIEWER';

export interface LoginResponse {
  token: string;
  tokenType: string;
  username: string;
  role: UserRole;
}
