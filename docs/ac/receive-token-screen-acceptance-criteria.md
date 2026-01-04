# Receive Token Screen - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Receive Token screen in the Mangala Wallet application. This screen displays a QR code containing the wallet address for receiving cryptocurrency tokens, with features for sharing, saving, and optionally including payment amounts in the QR data.

## Screen Parameters

### 1. Initialization Parameters

#### Required Parameters
- **Given** Screen initialization
- **When** Navigating to receive screen
- **Then** Must provide:
  - accountId: String? (account identifier)
  - address: String? (wallet address override)
  - networkType: NetworkType (EVM/Antelope)
  - initialBlockchainUid: String? (blockchain identifier)
  - onBackPressedButton: Callback for back navigation

#### Parameter Validation
- **Given** Parameters provided
- **When** Screen loads
- **Then** Should:
  - Use address if provided, otherwise fetch from accountId
  - Apply networkType for UI differentiation
  - Load correct blockchain configuration
  - Handle null values gracefully

## Visual Design

### 2. Screen Layout

#### Background Design
- **Given** Screen displays
- **When** Rendered
- **Then** Should show:
  - Dark QR background image (BackgroundDefaultQrDark)
  - Full screen coverage with ContentScale.FillBounds
  - Safe drawing padding respected
  - Gradient/patterned background for visual appeal

#### Content Structure
- **Given** Main content area
- **When** Displayed
- **Then** Should contain:
  - Top navigation bar (currently empty title)
  - Title and instruction section
  - QR code card (main feature)
  - Amount section (optional)
  - Warning message (bottom)
  - Action buttons row (save/copy/share)

### 3. Title Section

#### Title Display
- **Given** Token information available
- **When** Title rendered
- **Then** Should show:
  - "Receive [coinName] token" (localized)
  - Font: Size17SemiBold
  - Color: textPrimary
  - Center alignment

#### Instruction Text
- **Given** Below title
- **When** Displayed
- **Then** Should show:
  - "Scan the QR code or copy the address:" (localized)
  - Font: Size14Medium
  - Color: textSecondary
  - Center alignment

## QR Code Display

### 4. QR Code Card

#### Card Design
- **Given** QR code card
- **When** Rendered
- **Then** Should have:
  - Rounded corners (CornerRadius.Small)
  - Background: bgInnerCard color
  - Elevation: Medium
  - Padding: default vertical, large horizontal

#### Address Display
- **Given** Wallet address
- **When** Shown above QR
- **Then** Should:
  - Display full address text
  - Color: vaultBrandColor (blue)
  - Font: Size17SemiBold
  - Show placeholder if loading
  - Support long address wrapping *[Enhancement]*

### 5. QR Code Image

#### QR Code Generation
- **Given** QR data available
- **When** Generating QR
- **Then** Should:
  - Generate 180dp x 180dp QR code
  - Include wallet address
  - Optionally include amount
  - High contrast black/white
  - Error correction level: Medium *[Enhancement]*

#### Network Logo Overlay
- **Given** QR code displayed
- **When** Rendered
- **Then** Should show:
  - Network logo in center (50dp)
  - White circular background
  - LocalImage from network configuration
  - Maintain QR scanability

#### Loading State
- **Given** QR data not ready
- **When** Loading
- **Then** Should:
  - Show placeholder animation
  - Maintain layout structure
  - Smooth transition when loaded

### 6. Network Badges

#### Badge Display
- **Given** Below QR code
- **When** Rendered
- **Then** Should show:
  - Two badges side by side (180dp total width)
  - "Mangala" badge (blue: #4A6FE3)
  - Network name badge (cyan: #00B4D8)
  - Semi-transparent backgrounds (10% opacity)
  - Rounded corners (Medium)
  - Equal width distribution

#### Badge Styling
- **Given** Badge design
- **When** Applied
- **Then** Should have:
  - Font: Size12SemiBold
  - Center text alignment
  - Padding: half horizontal, quarter vertical
  - Clear readability

## Amount Features

### 7. Add Amount Button

#### Initial State (No Amount)
- **Given** No amount specified
- **When** Below QR card
- **Then** Should show:
  - "Add amount" button
  - Plus icon (IcAdd)
  - Text and icon in blue (textLink color)
  - TextButton style
  - Opens amount input sheet on tap

#### Amount Display (With Amount)
- **Given** Amount specified
- **When** Displayed
- **Then** Should show:
  - "[coinName] [amount]" format
  - Coin name in textPrimary
  - Amount in textLink color
  - Edit icon (IcEdit) if enabled
  - Font: Size17SemiBold

### 8. Amount Bottom Sheets

#### Add Amount Sheet
- **Given** Add amount tapped
- **When** Sheet opens
- **Then** Should:
  - Show AddAmountToReceiveQrScreen
  - Pass initial amount (empty)
  - Pass coin name and decimals
  - Update QR on save
  - Dismiss on completion

#### Edit Amount Sheet
- **Given** Edit amount tapped
- **When** Sheet opens
- **Then** Should show:
  - EditReceiveAmountScreen first
  - Options to edit or remove
  - Transition to AddAmountToReceiveQrScreen if edit chosen
  - 100ms delay between sheets
  - Clear amount if remove chosen

### 9. Amount in QR Code *[Enhancement]*

#### QR Data Format
- **Given** Amount included
- **When** Generating QR
- **Then** Should use:
  - EIP-681 format for EVM chains
  - Standard URI format with amount parameter
  - Proper decimal handling
  - Network-specific formatting

## Warning Section

### 10. Warning Message

#### Warning Card Design
- **Given** Bottom of content
- **When** Displayed
- **Then** Should have:
  - Full width with horizontal padding
  - bgInnerCard background
  - 1dp border (border color)
  - Rounded corners (Small)
  - Warning icon on left

#### Warning Content
- **Given** Warning text
- **When** Rendered
- **Then** Should show:
  - Title: "Only send [coinName] via the [networkName]"
  - Subtitle: Risk warning about wrong networks
  - Highlighted coin and network names (textPrimary)
  - Regular text in textSecondary
  - Proper text wrapping

#### Warning Styling
- **Given** Text styling
- **When** Applied
- **Then** Should use:
  - Title: Size12Regular
  - Subtitle: Size10Regular
  - Color coding for emphasis
  - Icon: Warning icon (unspecified tint)

## Action Buttons

### 11. Bottom Action Row

#### Button Layout
- **Given** Bottom of screen
- **When** Displayed
- **Then** Should show:
  - Three action buttons horizontally
  - Even spacing (SpaceEvenly)
  - Center alignment
  - Bottom padding (default)

#### Save Button
- **Given** Save action
- **When** Tapped
- **Then** Should:
  - Capture QR card area as image
  - Use graphics layer recording
  - Save to device gallery
  - Show success toast: "QR code saved successfully"
  - Show error toast on failure
  - Request permissions if needed *[Enhancement]*

#### Copy Button
- **Given** Copy action
- **When** Tapped
- **Then** Should:
  - Copy QR data to clipboard
  - Label: "Mangala copy"
  - Show toast: "Address copied"
  - Support long press for options *[Enhancement]*
  - Larger button size (44dp vs default)

#### Share Button
- **Given** Share action
- **When** Tapped
- **Then** Should:
  - Open system share sheet
  - Title: "Mangala share via"
  - Share QR data as text
  - Include amount if specified
  - Support image sharing *[Enhancement]*

### 12. Action Button Design

#### Button Appearance
- **Given** Action buttons
- **When** Rendered
- **Then** Should have:
  - Circular card container
  - bgInnerCard background
  - Medium elevation
  - Icon inside (iconPrimary color)
  - Text label below
  - Column layout with center alignment

#### Button Sizing
- **Given** Button dimensions
- **When** Applied
- **Then** Should use:
  - Default: RoundedActionIconButtonSize
  - Icon: IconSize (20dp for copy)
  - Consistent touch targets
  - Proper spacing between elements

## Navigation Features

### 13. Top Navigation

#### Back Navigation
- **Given** Back button pressed
- **When** Tapped
- **Then** Should:
  - Call onBackPressedButton callback
  - Pop navigation stack
  - Return to previous screen
  - Clean up resources

#### Edit Button *[Currently Commented Out]*
- **Given** Edit functionality
- **When** Implemented
- **Then** Should:
  - Show edit icon in top bar
  - Open account selection sheet
  - Allow switching accounts
  - Update QR for new selection

### 14. Bottom Sheet Navigation

#### Account Selection Sheet *[Enhancement]*
- **Given** Edit account tapped
- **When** Sheet opens
- **Then** Should:
  - Show ReceiveTokenPickAccountScreen
  - List available accounts
  - Filter by network type
  - Update on selection
  - Dismiss sheet after selection

## Network Support

### 15. Multi-Network Features

#### EVM Networks
- **Given** EVM network type
- **When** Displaying
- **Then** Should:
  - Show Ethereum-style address
  - Support EIP-681 QR format
  - Display network name correctly
  - Handle all EVM chains

#### Antelope Networks
- **Given** Antelope network type
- **When** Displaying
- **Then** Should:
  - Show Antelope account name
  - Custom QR format for Antelope
  - Proper memo field support *[Enhancement]*
  - Network-specific features

### 16. Network Switching *[Enhancement]*

#### Network Selector
- **Given** Multiple networks available
- **When** User needs different network
- **Then** Could provide:
  - Network dropdown/selector
  - Quick switch between compatible networks
  - Update QR instantly
  - Maintain amount if applicable
  - Show network fees

## Enhanced Features *[Enhancements]*

### 17. QR Code Customization

#### QR Code Styling Options
- **Given** Advanced features
- **When** Implemented
- **Then** Could offer:
  - Custom QR colors
  - Logo positioning options
  - Error correction levels
  - Size variations
  - Frame decorations

#### Dynamic QR Codes
- **Given** Enhanced security
- **When** Enabled
- **Then** Could provide:
  - Time-limited QR codes
  - One-time use addresses
  - Rotating addresses
  - Transaction-specific QRs
  - Expiry indicators

### 18. Payment Request Features

#### Invoice Generation
- **Given** Payment requests
- **When** Creating
- **Then** Could include:
  - Description/memo field
  - Due date
  - Invoice number
  - Tax information
  - Multiple currency display

#### Request Templates
- **Given** Frequent requests
- **When** Implemented
- **Then** Could offer:
  - Save request templates
  - Quick access to common amounts
  - Recurring request setup
  - Batch request creation
  - Category tagging

### 19. Share Enhancements

#### Rich Sharing
- **Given** Share functionality
- **When** Enhanced
- **Then** Could provide:
  - Share as image with QR
  - Share via email with template
  - Direct messaging app integration
  - NFC tap to share
  - Bluetooth proximity sharing

#### Share Tracking
- **Given** Shared addresses
- **When** Monitoring
- **Then** Could track:
  - Share history
  - Received confirmations
  - Payment status updates
  - Analytics on share methods
  - Success rates

### 20. Security Features

#### Address Verification
- **Given** Security concerns
- **When** Displaying address
- **Then** Could show:
  - Address checksum validation
  - Visual address verification
  - ENS/domain name support
  - Address book confirmation
  - Known scam warnings

#### Privacy Options
- **Given** Privacy needs
- **When** Enabled
- **Then** Could provide:
  - Blur address until tapped
  - Screen recording prevention
  - Watermarked QR codes
  - View-once addresses
  - Access logs

## Accessibility

### 21. Screen Reader Support

#### Content Announcements
- **Given** Screen reader active
- **When** Navigating
- **Then** Should announce:
  - "Receive [coin] screen"
  - Full address when focused
  - Amount if present
  - Warning message clearly
  - Action button purposes

#### QR Code Accessibility
- **Given** QR code displayed
- **When** Screen reader active
- **Then** Should:
  - Announce "QR code for address"
  - Provide text alternative
  - Skip decorative elements
  - Focus on actionable items
  - Clear navigation order

### 22. Visual Accessibility

#### High Contrast Mode
- **Given** High contrast enabled
- **When** Viewing
- **Then** Should provide:
  - Enhanced QR contrast
  - Clear text visibility
  - Distinct button boundaries
  - Visible warnings
  - Color-blind safe indicators

#### Text Scaling
- **Given** Large text enabled
- **When** Applied
- **Then** Should:
  - Scale text appropriately
  - Maintain QR code size
  - Wrap address text
  - Preserve layout integrity
  - Keep buttons accessible

## Testing Scenarios

### 23. Happy Path - Basic Receive

1. Navigate to receive screen
2. View loading placeholder briefly
3. See QR code with address
4. Read warning message
5. Tap copy button
6. See "Address copied" toast
7. Successfully receive funds

### 24. Happy Path - With Amount

1. Open receive screen
2. Tap "Add amount"
3. Enter amount in sheet
4. Save amount
5. See updated QR with amount
6. Share via system share
7. Complete transaction

### 25. Happy Path - Save QR

1. Display QR code
2. Tap save button
3. Grant gallery permission (first time)
4. See saving progress
5. Get success toast
6. Find image in gallery
7. Image contains correct QR

### 26. Edge Cases

#### Data Loading Edge Cases
- Slow network: Show placeholder appropriately
- Address change: Update QR smoothly
- Invalid address: Show error state
- Network timeout: Retry mechanism
- Offline mode: Cache last address

#### Interaction Edge Cases
- Double-tap buttons: Single action only
- Quick sheet open/close: Stable state
- Rotation during save: Complete action
- Background during share: Handle properly
- Memory pressure: Maintain functionality

## Performance Requirements

### 27. Loading Performance

- **Screen load**: < 200ms
- **QR generation**: < 100ms
- **Sheet animation**: < 300ms
- **Image capture**: < 500ms
- **Gallery save**: < 2 seconds

### 28. Resource Usage

- **Memory footprint**: < 50MB
- **QR library size**: Optimized
- **Image processing**: Efficient
- **CPU usage**: < 20% idle
- **Battery impact**: Minimal

## Security Requirements

### 29. Data Protection

- **Address validation**: Always verify
- **QR data integrity**: Checksums
- **Clipboard security**: Clear after timeout
- **Share data**: Sanitized
- **No sensitive data in analytics**

### 30. Privacy Protection

- **No address logging**: In production
- **Screen recording**: Prevention option
- **Secure share channels**: Preferred
- **Address rotation**: Support
- **Transaction privacy**: Maintained

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ QR code generation and display
✅ Wallet address display
✅ Network badges (Mangala + network)
✅ Add/edit amount functionality
✅ Save QR to gallery
✅ Copy address to clipboard
✅ Share via system share
✅ Warning message about networks
✅ Bottom sheet navigation
✅ Multi-network support (EVM/Antelope)

### Should Have (Reasonable Enhancements)
⭐ Account switching (currently commented out)
⭐ Permission handling for gallery
⭐ Address validation and checksums
⭐ Image sharing in addition to text
⭐ Long address handling
⭐ Network fee display
⭐ Memo/description field
⭐ Share history tracking
⭐ QR code error correction options
⭐ Accessibility improvements

### Nice to Have (Future Enhancements)
💡 Dynamic/rotating QR codes
💡 Invoice generation system
💡 Payment request templates
💡 ENS/domain name support
💡 NFC tap to share
💡 Rich email templates
💡 Time-limited QR codes
💡 Transaction status tracking
💡 Multiple currency display
💡 Advanced privacy options