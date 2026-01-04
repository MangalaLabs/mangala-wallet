# Step 3: Account Ready to Claim - Acceptance Criteria

## Overview
This document defines the acceptance criteria for Step 3 of the Vaulta account creation flow - the payment and confirmation screen. This screen presents the selected account name, explains the value proposition, handles payment processing, and prepares for account creation on the blockchain.

## Screen Structure

### 1. Navigation and Progress

#### Top Navigation Bar
- **Given** I am on Step 3 of account creation
- **When** I view the top bar
- **Then** I should see:
  - Back button (left side, 40dp circular touch area)
  - Step indicator showing 2 of 4 steps completed (50% progress)
  - Balanced spacing (40dp spacer on right)
  - Status bar padding respected

#### Back Navigation
- **Given** I am on the payment screen
- **When** I tap the back button
- **Then** I should:
  - Navigate back to Step 2 (account name selection)
  - Preserve the selected account name
  - Cancel any pending payment processes
  - See smooth navigation transition

#### Progress Indicator
- **Given** I am viewing the progress indicator
- **When** The screen loads
- **Then** I should see:
  - 4 total steps with 2 completed (50% progress)
  - Visual distinction between completed and pending steps
  - Accurate representation of user journey

### 2. Screen Layout

#### Visual Environment
- **Given** The screen has loaded
- **When** I view the layout
- **Then** I should see:
  - Onboarding gradient background with circle pattern
  - Safe area padding (navigation bar and IME)
  - Full-screen scrollable content
  - Staggered fade-in animations (200ms, 400ms, 600ms, 800ms delays)

## Main Content

### 3. Title and Description

#### Header Section
- **Given** I am viewing the header
- **When** The content loads
- **Then** I should see:
  - Title: "Ready to claim"
  - Font size: 20sp
  - Font weight: Medium
  - White color
  - Left alignment

#### Description Text
- **Given** I am reading the description
- **When** Below the title
- **Then** I should see:
  - Text: "You will only need to pay the fee once. Remember, this cannot be changed later"
  - Font size: 14sp
  - Color: #A5B4CB
  - Emphasis on permanence and one-time payment
  - 12dp spacing from title

### 4. Account Name Display Card

#### Card Design
- **Given** I am viewing my selected account
- **When** The card displays
- **Then** I should see:
  - Gradient background (blue to purple to red, 15% opacity)
  - 16dp border radius
  - Full width with 16dp horizontal padding
  - 12dp vertical, 16dp horizontal internal padding

#### Card Content
- **Given** The account card is displayed
- **When** I view its contents
- **Then** I should see:
  - Vaulta account icon (64dp size)
  - Account name with suffix (e.g., "alice.gm")
  - Font size: 17sp
  - Font weight: Medium
  - Color: #F1F5F9
  - Center alignment

#### Unconsumed Purchase Indicator
- **Given** I have an existing purchase for this account
- **When** The card displays
- **Then** I should see:
  - Green text: "You have an existing purchase for this account"
  - Font size: 12sp
  - Color: #10B981 (green)
  - Displayed below account name

### 5. What You're Getting Section

#### Section Title
- **Given** I am viewing the benefits section
- **When** Below the account card
- **Then** I should see:
  - Title: "What you're getting"
  - Font size: 17sp
  - Font weight: Medium
  - Color: #F1F5F9
  - Center alignment

#### Benefits List
- **Given** I am viewing the benefits
- **When** The list displays
- **Then** I should see these items:
  1. "Permanent blockchain identity"
  2. "Easy-to-share wallet address"
  3. "No renewal fees ever"
  4. "Transfer or sell anytime"
  - Purple check marks (#8647F3)
  - 16dp spacing between items
  - Font size: 12sp
  - Left-aligned text with check icons

## Purchase Flow

### 6. Purchase Button

#### Button Display - New Purchase
- **Given** I don't have an existing purchase
- **When** The button displays
- **Then** I should see:
  - Text: "Purchase for $[price]" (e.g., "Purchase for $4.99")
  - Primary button style (gradient)
  - Full width with 16dp horizontal padding
  - Dynamic price from IAP product

#### Button Display - Existing Purchase
- **Given** I have an unconsumed purchase
- **When** The button displays
- **Then** I should see:
  - Text: "Continue with existing purchase"
  - Same styling as new purchase button
  - No additional payment required

#### Button Action - New Purchase
- **Given** I don't have an existing purchase
- **When** I tap the purchase button
- **Then** The system should:
  1. Initiate platform-specific IAP flow
  2. Show loading state during processing
  3. Handle success/failure appropriately
  4. Navigate to Step 4 on success

#### Button Action - Existing Purchase
- **Given** I have an unconsumed purchase
- **When** I tap continue
- **Then** The system should:
  1. Verify the existing purchase
  2. Skip payment flow
  3. Navigate directly to Step 4
  4. Apply the existing purchase to this account

### 7. Why Does It Cost Money Link

#### Link Display
- **Given** I am viewing the purchase section
- **When** Below the purchase button
- **Then** I should see:
  - Text: "Why does it cost money?"
  - Font size: 14sp
  - Color: #3B90FF (blue)
  - Center alignment
  - Clickable with tap feedback

#### Link Action
- **Given** I tap the link
- **When** The action triggers
- **Then** I should see:
  - Bottom sheet slides up
  - Main content remains visible behind
  - Smooth animation

## Bottom Sheet - Why Does It Cost Money

### 8. Bottom Sheet Structure

#### Sheet Design
- **Given** The bottom sheet is open
- **When** I view the sheet
- **Then** I should see:
  - Dark background (#1D263E)
  - White content text
  - Drag handle (40dp x 4dp, 30% opacity white)
  - Rounded top corners
  - Scrollable content if needed

#### Sheet Title
- **Given** The sheet is open
- **When** I view the header
- **Then** I should see:
  - Text: "💭 Why $[price]?"
  - Font size: 24sp
  - Font weight: Bold
  - White color
  - Center alignment

#### Sheet Description
- **Given** I am reading the explanation
- **When** Below the title
- **Then** I should see:
  - Text: "Unlike regular wallets, Vaulta gives you a real identity on the blockchain."
  - Font size: 17sp
  - Color: #D1D1D1
  - Center alignment

### 9. Comparison Table

#### Table Structure
- **Given** I am viewing the comparison
- **When** The table displays
- **Then** I should see:
  - Dark background (#0F172A)
  - Border (#1E293B)
  - 12dp border radius
  - Three columns: Feature | Vaulta | Regular Wallets
  - Header row with gray background (#1E293B)

#### Table Content
- **Given** I am reading the comparison
- **When** I view each row
- **Then** I should see these features:

| Feature | Vaulta | Regular Wallets |
|---------|--------|-----------------|
| Easy to share | ✓ (green) | ✗ (red) |
| Human-readable | ✓ (green) | ✗ (red) |
| Permanent identity | ✓ (green) | ✗ (red) |
| No renewal fees | ✓ (green) | ✗ (red) |
| Can transfer/sell | ✓ (green) | ✗ (red) |
| Professional look | ✓ (green) | ✗ (red) |

#### Table Interaction
- **Given** The comparison table is displayed
- **When** I interact with it
- **Then** I should:
  - Be able to scroll if content exceeds viewport
  - See clear visual distinction between features
  - Understand value proposition clearly

#### Got It Button
- **Given** I've read the explanation
- **When** I want to close the sheet
- **Then** I should see:
  - "Got it" button at bottom
  - Primary button style
  - Full width
  - Closes sheet on tap

## Payment Processing

### 10. In-App Purchase Flow

#### iOS Payment
- **Given** I'm on iOS
- **When** I initiate purchase
- **Then** I should see:
  - Apple Pay sheet or App Store payment
  - Touch ID/Face ID authentication
  - Native iOS payment experience
  - Success/failure feedback

#### Android Payment
- **Given** I'm on Android
- **When** I initiate purchase
- **Then** I should see:
  - Google Play payment sheet
  - Fingerprint/PIN authentication
  - Native Android payment experience
  - Success/failure feedback

#### Desktop Payment *[Enhancement]*
- **Given** I'm on desktop
- **When** I initiate purchase
- **Then** I should see:
  - External payment provider (Stripe/PayPal)
  - Web-based payment form
  - Secure payment processing
  - Return to app after completion

### 11. Purchase States

#### Loading State
- **Given** Payment is processing
- **When** Waiting for response
- **Then** I should see:
  - Loading indicator on button
  - Disabled state for all interactions
  - *[Enhancement]* Estimated time remaining
  - *[Enhancement]* Cancel option after 30 seconds

#### Success State
- **Given** Payment completed successfully
- **When** Transaction confirmed
- **Then** I should:
  - See success animation *[Enhancement]*
  - Auto-navigate to Step 4
  - Receive purchase confirmation
  - Have purchase saved for recovery

#### Failure States
- **Given** Payment fails
- **When** Error occurs
- **Then** I should see appropriate message:
  - "Payment cancelled" - User cancelled
  - "Payment failed" - Transaction error
  - "Network error" - Connection issue
  - "Product unavailable" - Store issue
  - Retry option for each error type

### 12. Product Already Owned Dialog

#### Dialog Display
- **Given** I try to purchase an already owned product
- **When** The system detects duplicate
- **Then** I should see:
  - Modal dialog overlay
  - Account name displayed
  - Explanation of the situation
  - Options to proceed

#### Dialog Actions
- **Given** The dialog is shown
- **When** I choose an action
- **Then** I can:
  - Continue with existing purchase
  - Cancel and go back
  - *[Enhancement]* View purchase history
  - *[Enhancement]* Contact support

## Advanced Features *[Enhancements]*

### 13. Pricing Tiers Display

#### Dynamic Pricing
- **Given** I have a premium account
- **When** Viewing the price
- **Then** I should see:
  - Price based on name length
  - Original and discounted price (if applicable)
  - Price in local currency
  - Tax information if applicable

#### Price Breakdown
- **Given** I want pricing details
- **When** I tap on price *[Enhancement]*
- **Then** I should see:
  - Base account cost
  - Premium name fee
  - Resource allocation cost
  - Total with tax

### 14. Alternative Payment Methods

#### Crypto Payment *[Enhancement]*
- **Given** I prefer crypto payment
- **When** Selecting payment method
- **Then** I should be able to:
  - Choose crypto payment option
  - See supported cryptocurrencies
  - View exchange rates
  - Complete payment via wallet

#### Voucher/Promo Code *[Enhancement]*
- **Given** I have a promo code
- **When** On the payment screen
- **Then** I should be able to:
  - Enter promo code
  - See discount applied
  - Validate code in real-time
  - Proceed with discounted price

### 15. Purchase Recovery

#### Restore Purchase
- **Given** I've purchased before
- **When** On a new device
- **Then** I should be able to:
  - Restore previous purchase
  - Link to existing account
  - Skip redundant payment
  - Continue account creation

#### Purchase History *[Enhancement]*
- **Given** I want to see past purchases
- **When** I access history
- **Then** I should see:
  - List of all purchases
  - Status of each (consumed/unconsumed)
  - Transaction IDs
  - Dates and amounts

### 16. Resource Allocation Preview *[Enhancement]*

#### Resource Display
- **Given** I'm about to purchase
- **When** Viewing the screen
- **Then** I should see:
  - RAM allocation (e.g., 3KB)
  - CPU stake amount
  - NET stake amount
  - What these resources enable

#### Resource Calculator
- **Given** I want more resources
- **When** I interact with calculator
- **Then** I can:
  - Adjust resource amounts
  - See price changes
  - Understand resource needs
  - Optimize for use case

## Security and Compliance

### 17. Purchase Verification

#### Receipt Validation
- **Given** A purchase is made
- **When** Processing payment
- **Then** The system should:
  - Verify receipt with store
  - Validate purchase signature
  - Store securely for recovery
  - Prevent replay attacks

#### Fraud Prevention *[Enhancement]*
- **Given** Suspicious activity detected
- **When** Processing payment
- **Then** The system should:
  - Flag unusual patterns
  - Require additional verification
  - Log for review
  - Protect user funds

### 18. Refund Policy

#### Refund Information *[Enhancement]*
- **Given** I want refund details
- **When** On the payment screen
- **Then** I should see:
  - Link to refund policy
  - Clear terms (e.g., "No refunds after blockchain creation")
  - Support contact
  - FAQ section

#### Refund Process *[Enhancement]*
- **Given** I need a refund
- **When** Before blockchain creation
- **Then** I should be able to:
  - Request refund in-app
  - Provide reason
  - Track refund status
  - Receive confirmation

## Accessibility

### 19. Screen Reader Support

#### Content Announcement
- **All text** must be accessible
- **Price information** clearly announced
- **Benefits list** read as complete list
- **Comparison table** navigable by row/column
- **Payment status** updates announced

### 20. Visual Accessibility

#### High Contrast Mode *[Enhancement]*
- **Given** High contrast is enabled
- **When** Viewing the screen
- **Then** I should see:
  - Enhanced contrast ratios
  - Clear visual boundaries
  - Readable text on all backgrounds
  - Distinct interactive elements

## Error Handling

### 21. Network Errors

#### Price Loading Failure
- **Given** Can't fetch product price
- **When** Screen loads
- **Then** I should see:
  - "Price unavailable" message
  - Retry button
  - Cached price if available
  - Contact support option

#### Purchase Network Failure
- **Given** Network fails during purchase
- **When** Transaction in progress
- **Then** The system should:
  - Save transaction state
  - Retry automatically (3 attempts)
  - Show clear error message
  - Provide manual retry option

### 22. Store Errors

#### Product Not Found
- **Given** IAP product missing
- **When** Loading products
- **Then** I should see:
  - Error message
  - Alternative payment suggestion
  - Support contact
  - Diagnostic information

## Performance Requirements

### 23. Loading Times

- **Screen load**: < 200ms
- **Product price fetch**: < 2 seconds
- **Animation frame rate**: 60 FPS
- **Payment sheet display**: < 500ms
- **Navigation after purchase**: < 1 second

### 24. Optimization

- **Cache product information**: During session
- **Preload payment UI**: Before button tap
- **Optimize animations**: GPU acceleration
- **Minimize network calls**: Batch requests

## Testing Scenarios

### 25. Happy Path
1. View account name card
2. Read benefits list
3. Check price ($4.99)
4. Tap "Why does it cost money?"
5. Read comparison table
6. Close bottom sheet
7. Tap purchase button
8. Complete payment
9. Navigate to Step 4

### 26. Edge Cases
- **Multiple purchases**: Handle gracefully
- **Price changes**: Update in real-time
- **Currency changes**: Reflect immediately
- **Airplane mode**: Show cached data
- **Low storage**: Handle payment failure
- **Background during payment**: Resume properly

### 27. Security Testing
- **Receipt tampering**: Validate server-side
- **Price manipulation**: Verify with store
- **Replay attacks**: Use nonces
- **Man-in-the-middle**: Certificate pinning

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ Account name display with gradient card
✅ Benefits list ("What you're getting")
✅ Dynamic pricing from IAP
✅ Purchase button with price
✅ "Why does it cost money?" explanation
✅ Comparison table (Vaulta vs Regular wallets)
✅ Unconsumed purchase detection
✅ Platform-specific payment flows
✅ Navigation to Step 4 after purchase
✅ Product already owned handling

### Should Have (Reasonable Enhancements)
⭐ Multiple payment methods (crypto, vouchers)
⭐ Price breakdown display
⭐ Purchase history view
⭐ Resource allocation preview
⭐ Refund policy and process
⭐ Success animations
⭐ Retry mechanisms for failures
⭐ Desktop payment support
⭐ Currency conversion display

### Nice to Have (Future Enhancements)
💡 Dynamic pricing based on demand
💡 Bulk account purchase discounts
💡 Referral program integration
💡 Subscription options for multiple accounts
💡 NFT receipt generation
💡 Cross-chain payment support
💡 Loyalty rewards system
💡 A/B testing for pricing