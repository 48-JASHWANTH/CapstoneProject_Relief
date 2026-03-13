export interface PaymentResponse {
  id: number;
  policyId: number;
  policyNumber: string;
  paymentType: string;
  amount: number;
  paymentStatus: string;
  paymentDate: string;
}

export interface PremiumPaymentRequest {
  policyId: number;
}
