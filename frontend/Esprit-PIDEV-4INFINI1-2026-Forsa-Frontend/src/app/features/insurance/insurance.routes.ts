import { Routes } from '@angular/router';

export const insuranceRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./insurance-hub/insurance-hub.component').then((m) => m.InsuranceHubComponent),
  },
  // ── Products ─────────────────────────────────────────────────────────────
  {
    path: 'products',
    loadComponent: () =>
      import('./insurance-products/pages/product-list/product-list.component').then(
        (m) => m.ProductListComponent,
      ),
  },
  {
    path: 'products/new',
    loadComponent: () =>
      import('./insurance-products/pages/product-form/product-form.component').then(
        (m) => m.ProductFormComponent,
      ),
  },
  {
    path: 'products/:id',
    loadComponent: () =>
      import('./insurance-products/pages/product-detail/product-detail.component').then(
        (m) => m.ProductDetailComponent,
      ),
  },
  {
    path: 'products/:id/edit',
    loadComponent: () =>
      import('./insurance-products/pages/product-form/product-form.component').then(
        (m) => m.ProductFormComponent,
      ),
  },
  // ── Policies ─────────────────────────────────────────────────────────────
  {
    path: 'policies',
    loadComponent: () =>
      import('./insurance-premium/pages/policy-list/policy-list.component').then(
        (m) => m.PolicyListComponent,
      ),
  },
  {
    path: 'policies/apply',
    loadComponent: () =>
      import('./insurance-premium/pages/policy-form/policy-form.component').then(
        (m) => m.PolicyFormComponent,
      ),
  },
  {
    path: 'policies/:id',
    loadComponent: () =>
      import('./insurance-premium/pages/policy-detail/policy-detail.component').then(
        (m) => m.PolicyDetailComponent,
      ),
  },
  // ── Claims ───────────────────────────────────────────────────────────────
  {
    path: 'claims',
    loadComponent: () =>
      import('./insurance-claims/pages/claim-list/claim-list.component').then(
        (m) => m.ClaimListComponent,
      ),
  },
  {
    path: 'claims/new',
    loadComponent: () =>
      import('./insurance-claims/pages/claim-form/claim-form.component').then(
        (m) => m.ClaimFormComponent,
      ),
  },
  {
    path: 'claims/:id',
    loadComponent: () =>
      import('./insurance-claims/pages/claim-detail/claim-detail.component').then(
        (m) => m.ClaimDetailComponent,
      ),
  },
  {
    path: 'claims/:id/edit',
    loadComponent: () =>
      import('./insurance-claims/pages/claim-form/claim-form.component').then(
        (m) => m.ClaimFormComponent,
      ),
  },
  // ── Premium Payments ─────────────────────────────────────────────────────
  {
    path: 'payments',
    loadComponent: () =>
      import(
        './insurance-premium-payements/pages/premium-payement-list/premium-payement-list.component'
      ).then((m) => m.PremiumPayementListComponent),
  },
  {
    path: 'payments/new',
    loadComponent: () =>
      import(
        './insurance-premium-payements/pages/premium-payements-form/premium-payements-form.component'
      ).then((m) => m.PremiumPayementsFormComponent),
  },
  {
    path: 'payments/:id',
    loadComponent: () =>
      import(
        './insurance-premium-payements/pages/premium-payement-detail/premium-payement-detail.component'
      ).then((m) => m.PremiumPayementDetailComponent),
  },
  {
    path: 'payments/:id/edit',
    loadComponent: () =>
      import(
        './insurance-premium-payements/pages/premium-payements-form/premium-payements-form.component'
      ).then((m) => m.PremiumPayementsFormComponent),
  },
];
