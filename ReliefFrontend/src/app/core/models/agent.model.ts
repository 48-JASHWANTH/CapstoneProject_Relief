export interface AgentResponse {
  id: number;
  userId: number;
  userName: string;
  userEmail: string;
  licenseNumber: string;
  region: string;
  totalPolicies: number;
}

export interface AgentRequest {
  userId: number;
  licenseNumber: string;
  region: string;
}
