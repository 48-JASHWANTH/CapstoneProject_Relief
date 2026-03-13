export interface DisasterZoneResponse {
  id: number;
  zoneName: string;
  location: string;
  riskLevel: string;
  disasterType: string;
  totalPolicies: number;
}

export interface DisasterZoneRequest {
  zoneName: string;
  location: string;
  riskLevel: string;
  disasterType: string;
}
