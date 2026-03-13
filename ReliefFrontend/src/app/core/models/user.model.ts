export interface UserResponse {
  id: number;
  name: string;
  email: string;
  status: string;
  role: string;
  createdAt: string;
}

export interface UserStatusRequest {
  status: string;
}

export interface AssignRolesRequest {
  userId: number;
  roleName: string;
}
