# Debugging Passkey Authentication Issue

## Current Issue
The passkey authentication is failing with 401 Unauthorized from Hanko, even though:
- The credential ID exists (returned by `/auth/login/initialize`)
- The client-side authentication works correctly
- The challenge and signatures are valid

## Error Details
```
Login failed for assertion oy_NLB_GlAq1doDQ4YqJuA: WebAuthn verification failed: [401 Unauthorized]
```

## Debugging Steps

### 1. Verify User Registration
Check if the user `btceth@gmail.com` is properly registered in Hanko:

1. Log into [Hanko Cloud Console](https://cloud.hanko.io)
2. Select project `db49b0a6-3c02-483b-b8b2-53a368b000e0`
3. Go to Users section
4. Search for `btceth@gmail.com`
5. Check if the user has a credential with ID `oy_NLB_GlAq1doDQ4YqJuA`

### 2. Check Hanko Logs
In Hanko Cloud Console:
1. Go to Logs or Activity section
2. Look for failed authentication attempts
3. Check the detailed error message from Hanko

### 3. Verify Registration Flow
Ensure the registration completed successfully:
1. Clear app data: `adb shell pm clear com.mangala.wallet.speedrun`
2. Register a new user with a different email
3. Note the credential ID returned
4. Try to login immediately with the same email

### 4. Backend Debugging
Add these debug endpoints to your backend:

```java
// Check if user exists in Hanko
@GetMapping("/debug/user/{email}")
public ResponseEntity<?> checkUser(@PathVariable String email) {
    // Call Hanko API to check if user exists
    // Return user details and credentials
}

// List all credentials for a user
@GetMapping("/debug/credentials/{email}")
public ResponseEntity<?> listCredentials(@PathVariable String email) {
    // Call Hanko API to list user's credentials
    // Return credential IDs and metadata
}
```

### 5. Common Causes

#### A. User Not Found
The email `btceth@gmail.com` might not exist in Hanko. This happens if:
- Registration failed silently
- User was deleted
- Wrong project ID

#### B. Credential Mismatch
The credential exists but belongs to a different user:
- User registered with different email
- Email vs username mismatch

#### C. Environment Issue
- Production vs staging Hanko project
- Different API keys between registration and login

### 6. Quick Fix Attempts

1. **Re-register the user**:
   - Clear app data
   - Register with `btceth@gmail.com` again
   - Note the new credential ID
   - Try login immediately

2. **Check Hanko Dashboard**:
   - Verify the user exists
   - Check credential details
   - Look for any flags or issues

3. **Test with Hanko's Demo**:
   - Try Hanko's demo app to verify your account works
   - Compare the requests with your implementation

### 7. Contact Hanko Support
If the issue persists, contact Hanko with:
- Project ID: `db49b0a6-3c02-483b-b8b2-53a368b000e0`
- User email: `btceth@gmail.com`
- Credential ID: `oy_NLB_GlAq1doDQ4YqJuA`
- Error: 401 Unauthorized during login finalization
- Request them to check why this specific credential is failing

## Temporary Workaround
While debugging, you can:
1. Create a test user with a new email
2. Document the exact registration process
3. Verify login works immediately after registration
4. If it works with new users but not old ones, the issue is with existing data