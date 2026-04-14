import { ClaimStatus, PaymentStatus, PolicyStatus } from '../enums/insurance.enums';

export interface InsuranceProduct {
  id?: number;
  productName: string;
  policyType: string;
  description?: string;
  premiumAmount: number;
  coverageLimit: number;
  durationMonths: number;
  isActive?: boolean;
  policies?: InsurancePolicy[];
}

export interface InsurancePolicy {
  id?: number;
  policyNumber?: string;
  premiumAmount?: number;
  coverageLimit?: number;
  startDate?: string;
  endDate?: string;
  nextPremiumDueDate?: string;
  status?: PolicyStatus;
  // Actuarial fields
  purePremium?: number;
  inventoryPremium?: number;
  commercialPremium?: number;
  finalPremium?: number;
  // Risk assessment
  riskScore?: number;
  riskCategory?: string;
  riskCoefficient?: number;
  // Payment details
  paymentFrequency?: string;
  periodicPaymentAmount?: number;
  numberOfPayments?: number;
  effectiveAnnualRate?: number;
  calculationNotes?: string;
  // Relations
  insuranceProduct?: InsuranceProduct;
  premiumPayments?: PremiumPayment[];
  claims?: InsuranceClaim[];
}

export interface InsurancePolicyApplicationDTO {
  productId: number;
  requestedCoverageLimit?: number;
  paymentFrequency?: string;
  notes?: string;
}

export interface InsuranceClaim {
  id?: number;
  claimNumber?: string;
  claimDate?: string;
  incidentDate?: string;
  claimAmount?: number;
  approvedAmount?: number;
  description?: string;
  status?: ClaimStatus;
  indemnificationPaid?: number;
  insurancePolicy?: { id: number; policyNumber?: string };
}

export interface PremiumPayment {
  id?: number;
  amount: number;
  dueDate?: string;
  paidDate?: string;
  status?: PaymentStatus;
  transactionId?: number;
  insurancePolicy?: { id: number; policyNumber?: string };
}
