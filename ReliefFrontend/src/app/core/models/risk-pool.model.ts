export interface RiskPoolResponse {
  id: number;
  disasterType: string;
  totalPremiumCollected: number;
  totalClaimsPaid: number;
  thresholdPercentage: number;
  poolStatus: string;
  criticalFlag: boolean;
  totalPolicies: number;
  totalClaims: number;
}

export interface RiskPoolRequest {
  disasterType: string;
  totalPremiumCollected: number;
  totalClaimsPaid: number;
  thresholdPercentage: number;
  poolStatus: string;
}
