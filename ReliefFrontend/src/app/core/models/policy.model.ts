export interface PolicyResponse {
  id: number;
  policyNumber: string;
  userId: number;
  userName: string;
  agentId: number | null;
  agentName: string | null;
  disasterType: string;
  policyType: string;
  propertyAddress: string;
  propertyValue: number;
  sumInsured: number;
  premiumAmount: number;
  status: string;
  remarks: string | null;
  startDate: string;
  endDate: string;
  nextPremiumDueDate?: string;
  region: string | null;
  tenure: number | null;
  disasterZoneId: number | null;
  disasterZoneName: string | null;
  disasterZoneRiskFactor: number | null;
  riskPoolDisasterType: string | null;
  yearBuilt?: number;
  constructionMaterial?: string;
  previousClaimsHistory?: string;
  safetyFeatures?: string;
  documents?: PolicyDocumentResponse[];
}

export interface PolicyDocumentResponse {
  id: number;
  policyId: number;
  documentType: string;
  fileUrl: string;
  documentStatus: string;
  agentRemarks: string | null;
  uploadedAt: string;
}

export interface PolicyAdvancedDetailsRequest {
  yearBuilt: number;
  constructionMaterial: string;
  previousClaimsHistory: string;
  safetyFeatures: string;
}

export interface PolicyApprovalRequest {
  status: string;
  remarks: string;
}

export interface UserPolicyRequest {
  disasterType: string;
  policyType: string;
  propertyAddress: string;
  propertyValue: number;
  sumInsured: number;
  region: string;
  tenure: number;
}

export interface DisasterZoneResponse {
  id: number;
  zoneName: string;
  location: string;
  riskLevel: string;
  disasterType: string;
  riskFactor: number;
  totalPolicies: number;
}
