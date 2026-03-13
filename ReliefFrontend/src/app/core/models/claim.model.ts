export interface ClaimResponse {
  id: number;
  claimNumber: string;
  policyId: number;
  policyNumber: string;
  description: string;
  estimatedLoss: number;
  approvedAmount: number | null;
  status: string;
  officerRemarks: string | null;
  filedDate: string;
  resolvedDate: string | null;
}

export interface ClaimRequest {
  policyId: number;
  description: string;
  estimatedLoss: number;
}

export interface ClaimDecisionRequest {
  decision: string;
  approvedAmount: number;
  remarks: string;
}
