# Complete Detailed Workflow: From Policy Request to Premium Payment

This document provides a line-to-line, word-to-word explanation of the entire workflow when a Customer applies for a new insurance policy and later makes a premium payment to activate it. We will trace the journey from the Angular Frontend to the Spring Boot Backend.

---

## Part 1: Making a Policy Request (Application)

When a customer navigates to the "Apply for New Policy" page, they use the [CustomerNewPolicy](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-new-policy/customer-new-policy.ts#21-83) component.

### Frontend: HTML Configuration [customer-new-policy.html](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-new-policy/customer-new-policy.html)
The HTML uses Angular's **Reactive Forms** to bind the input fields.

1. `<form [formGroup]="form" (ngSubmit)="submit()">`: Links the HTML form to the Angular `FormGroup` called `form` and triggers the [submit()](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-new-policy/customer-new-policy.ts#66-77) function when the user clicks 'Submit Application'.
2. `formControlName="disasterType"`, `formControlName="region"`, `formControlName="policyType"`, etc.: These directives bind individual HTML `<select>`, `<textarea>`, and `<input>` elements directly to the `FormControl` objects in the TypeScript component.
3. `@if (invalid('fieldInfo'))`: These blocks dynamically render red validation error messages (like `text-red-500`) below the inputs if the field is manipulated and is invalid (e.g., if sum insured exceeds property value).
4. [(input)="onPropertyValueChange()"](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-policies/customer-policies.ts#33-41): Binds the Property Value number input to a real-time recalculation function, ensuring the 'Sum Insured' dynamically re-validates.
5. `<button type="submit" [disabled]="submitting()">`: The final submit button. It disables itself dynamically and shows a loading spinner if the `submitting()` signal is set to true.

### Frontend: TypeScript Logic [customer-new-policy.ts](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-new-policy/customer-new-policy.ts)
The brains of the form component handling validation and service dispatch.

1. **[sumInsuredValidator](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-new-policy/customer-new-policy.ts#8-18)**: A custom validation function. It fetches the parent form group. It checks if the [sumInsured](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-new-policy/customer-new-policy.ts#8-18) entered by the user exceeds the `propertyValue`. If it does, it returns a validation error object `{ exceedsPropertyValue: true }`, which the HTML listens for to display the error text.
2. **Setup Fields**: The class injects `FormBuilder`, [CustomerPolicyService](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/services/customer-policy.ts#42-59), `Router`, and `AuthService`. It also defines static dropdown data: `disasterTypes`, `regions`, `tenureOptions`, and `policyTypes`.
3. **[ngOnInit()](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-policies/customer-policies.ts#31-32)**: Initializes the Reactive Form (`this.form = this.fb.group(...)`). It attaches strict validations (e.g., `Validators.required`, `Validators.min(1)`, and our custom [sumInsuredValidator](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-new-policy/customer-new-policy.ts#8-18)).
4. **[onPropertyValueChange()](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-new-policy/customer-new-policy.ts#60-63)**: Manually triggers validation on [sumInsured](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-new-policy/customer-new-policy.ts#8-18) every time `propertyValue` changes.
5. **[submit()](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-new-policy/customer-new-policy.ts#66-77)**: The core submission function.
   - `if (this.form.invalid)`: Checks if the form has errors. If so, it marks all fields as touched (forcing error messages to display) and aborts early via `return`.
   - `this.submitting.set(true)`: Sets the UI loading state to true.
   - `this.svc.submitPolicy(this.userId, this.form.value)`: Passes the logged-in user's ID and the entire form JSON payload to the [CustomerPolicyService](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/services/customer-policy.ts#42-59).
   - `.subscribe(() => { ... })`: Waits for the backend response asynchronously. Once successful, it stops the loader and navigates the user back to the `/customer/policies` list dashboard.

### Frontend: Service Layer [customer-policy.ts](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/services/customer-policy.ts)
1. **[submitPolicy(userId: number, req: UserPolicyRequest)](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/services/customer-policy.ts#55-58)**: 
   - Uses Angular's `HttpClient` (`this.http`).
   - Executes a `POST` HTTP request to `${API_BASE_URL}/api/users/${userId}/policies`.
   - Transmits the `req` variable (the form data) directly in the HTTP request body. Converts the backend JSON response back into an RxJS Observable of type [PolicyResponse](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/services/customer-policy.ts#6-30).

---

### Backend: Controller Layer [UserPolicyController.java](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/Relief/src/main/java/org/hartford/relief/controller/userController/UserPolicyController.java)
The backend receives the HTTP request here.

1. **`@PostMapping`**: Listens for HTTP POST requests at `/api/users/{userId}/policies`.
2. **[submitPolicy(@PathVariable Long userId, @RequestBody UserPolicyRequest request)](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/services/customer-policy.ts#55-58)**: 
   - Extracts `userId` from the URL path.
   - Deserializes the incoming raw JSON payload into the [UserPolicyRequest](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/services/customer-policy.ts#31-41) Java Data Transfer Object (DTO).
3. **`return ResponseEntity.status(HttpStatus.CREATED).body(...)`**: Passes execution to the service layer. Upon completion, packages the returned [PolicyResponse](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/services/customer-policy.ts#6-30) into an HTTP 201 Created format and sends it back to the Angular frontend.

### Backend: Service Layer [UserPolicyServiceImpl.java](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/Relief/src/main/java/org/hartford/relief/service/impl/userServiceImpl/UserPolicyServiceImpl.java)
The heavy lifting and business logic execution.

1. **[submitPolicy(Long userId, UserPolicyRequest request)](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/services/customer-policy.ts#55-58)**: This is marked `@Transactional` so any DB failure rolls back the entire process.
2. **User lookup**: `userRepository.findById(userId)`. Triggers an exception if the user does not exist.
3. **Validation blocks**: Manually checks if `DisasterType`, `Region`, `Tenure`, or `SumInsured` are empty/null/negative, throwing `BadRequestException` if so.
4. **Disaster Zone Mapping**: Scans the `disasterZoneRepository` to lock onto the specific geographical zone based on the `disasterType` and `region` requested on the frontend.
5. **Risk Factor Calculation**: If the DB provides a risk factor for the zone, it uses it. Otherwise, it uses a hardcoded fallback algorithm via a Switch-Case (e.g., Earthquakes = 6.5, Floods = 5.0).
6. **Risk Pool Locator**: Looks up the active financial `RiskPool` linked to that disaster.
7. **Premium Verification**: Checks if the customer manually attached a premium, otherwise it calculates it using: `Premium = sumInsured * baseRate * (riskFactor / 5.0)`.
8. **Policy Number Generation**: Creates a randomized unique string: `"POL-" + UUID.randomUUID()`.
9. **Builder Pattern (`Policy.builder()`)**: Assembles the actual [Policy](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/services/customer-policy.ts#55-58) database Entity objects mapping the request fields (Address, sumInsured, etc.). Notably, sets the initial [Status](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/Relief/src/main/java/org/hartford/relief/service/impl/userServiceImpl/UserPolicyServiceImpl.java#136-143) to `"PENDING"`.
10. **`policyRepository.save(policy)`**: Persists the row physically in the [Policy](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/services/customer-policy.ts#55-58) relational database table.
11. **[mapToResponse(policy)](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/Relief/src/main/java/org/hartford/relief/service/impl/userServiceImpl/UserPolicyServiceImpl.java#144-171)**: Translates the DB Entity back into the clean [PolicyResponse](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/services/customer-policy.ts#6-30) DTO for the frontend.

---
---

## Part 2: Making a Premium Payment

Once the Admin/Agent approves the Customer's policy, its status switches to `"APPROVED"`. The Customer can now pay the premium to make the policy `"ACTIVE"`.

### Frontend: HTML Configuration [customer-policies.html](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-policies/customer-policies.html)
1. The page iterates over a list of policies using `@for (p of filtered(); track p.id)`.
2. **Action Cell**: Inside the data table's column:
   ```html
   @if (p.status === 'APPROVED') {
     <button (click)="openPay(p)">Pay Premium</button>
   }
   ```
   If the policy status is APPROVED, it renders a green "Pay Premium" button.
3. Clicking it triggers the [openPay(p)](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-policies/customer-policies.ts#48-52) function, passing in the specific policy object.
4. An external dialog `<app-pay-premium-dialog>` is displayed conditionally via `@if (showPayDialog() ...)`.

### Frontend: TypeScript Logic [customer-policies.ts](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-policies/customer-policies.ts)
1. **[openPay(p: PolicyResponse)](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-policies/customer-policies.ts#48-52)**: 
   - Sets `this.selectedPolicy.set(p)` tracking the exact policy the customer wants to pay for.
   - Sets `this.showPayDialog.set(true)`, causing the popup dialog to render on screen.
2. **[onPayConfirmed()](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-policies/customer-policies.ts#53-60)**: Bound to the dialog's confirmation event.
   - Calls `paymentSvc.payPremium(this.userId, this.selectedPolicy()!.id)`.
   - On successful response `.subscribe(() => {...})`, it:
     1. Closes the dialog (`showPayDialog.set(false)`).
     2. Refreshes the policy data table ([load()](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-policies/customer-policies.ts#33-41)).
     3. Triggers a UI success banner ([toast()](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/policies/components/customer-policies/customer-policies.ts#72-76)).

### Frontend: Service Layer [customer-payment.ts](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/payments/services/customer-payment.ts)
1. **[payPremium(userId: number, policyId: number)](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/Relief/src/main/java/org/hartford/relief/service/impl/userServiceImpl/UserPaymentServiceImpl.java#34-88)**:
   - Executes an HTTP `POST` to `${API_BASE_URL}/api/users/${userId}/payments/pay-premium`.
   - Sends a simple JSON payload containing only the ID of the policy: `{ policyId }`.

---

### Backend: Controller Layer [UserPaymentController.java](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/Relief/src/main/java/org/hartford/relief/controller/userController/UserPaymentController.java)
1. **`@PostMapping("/pay-premium")`**: Listens for the post request under the specific user's URI context.
2. **[payPremium(...)](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/Relief/src/main/java/org/hartford/relief/service/impl/userServiceImpl/UserPaymentServiceImpl.java#34-88)**: Intercepts the `PremiumPaymentRequest` DTO and forwards it directly to `userPaymentService`. Returns an HTTP 201 CREATED format upon success.

### Backend: Service Layer [UserPaymentServiceImpl.java](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/Relief/src/main/java/org/hartford/relief/service/impl/userServiceImpl/UserPaymentServiceImpl.java)
This is highly sensitive transactional logic handling money and state modifications.

1. **[payPremium(Long userId, PremiumPaymentRequest request)](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/Relief/src/main/java/org/hartford/relief/service/impl/userServiceImpl/UserPaymentServiceImpl.java#34-88)**: Marked `@Transactional`.
2. **Entity Lookups**: 
   - Fetches the User.
   - Fetches the exact Policy via `policyRepository.findById(request.getPolicyId())`.
3. **Security Check (Ownership)**: 
   - `if (!policy.getUser().getId().equals(userId))` -> Throws `UnauthorizedAccessException`. It ensures a user cannot maliciously pass the policy ID of somebody else to pay.
4. **State Machine Verification**:
   - `if (!"APPROVED".equalsIgnoreCase(policy.getStatus()))` -> Ensures payment can ONLY occur if the policy was properly vetted and approved. Throws `InvalidStatusTransitionException` otherwise.
5. **Duplicate Payment Prevention**:
   - `paymentRepository.findByPolicyIdAndPaymentType` queries the DB. If a premium row already exists, it throws `PremiumAlreadyPaidException`.
6. **Payment Record Creation**:
   - Uses `Payment.builder()` to create a real financial record.
   - Sets `paymentType("PREMIUM")` and `amount(policy.getPremiumAmount())`.
   - Sets `paymentStatus("COMPLETED")` immediately.
7. **`paymentRepository.save(payment)`**: Saves the payment record to DB.
8. **Policy Activation**:
   - `policy.setStatus("ACTIVE")`: Modifies the attached policy state.
   - `policyRepository.save(policy)`: Persists the new ACTIVE state.
9. **Risk Pool Funding**:
   - Grabs the matching `RiskPool` attached to the policy.
   - Adds the new paid amount to the pool's total reserves: `pool.setTotalPremiumCollected(current + policy.getPremiumAmount())`.
   - `riskPoolRepository.save(pool)`: Updates the total pool funds.
10. **Return Result**: Maps the DB entity to [PaymentResponse](file:///c:/Users/ratna/OneDrive/Desktop/hartford/CAPSTONE_PROJECT/ReliefFrontend/src/app/features/customer/payments/services/customer-payment.ts#6-15) and returns it to the frontend to close the loop smoothly.
