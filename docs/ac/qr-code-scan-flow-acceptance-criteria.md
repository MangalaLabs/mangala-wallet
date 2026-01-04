# QR Code Scan Flow - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the QR Code Scanning and Receiving functionality in the Mangala Wallet application. The QR code system provides seamless cryptocurrency transactions through visual code scanning, supporting multiple QR code types including payment addresses, transaction signing, WalletConnect sessions, and account synchronization. The implementation spans across Android, iOS, and Desktop platforms with native camera integration.

## Critical Implementation Note

### 1. Current Implementation Status

#### Core Components Implemented
- **Given** The current implementation
- **When** Reviewing the codebase
- **Then** Note that:
  - `ScanQRCode.kt`: Platform-specific scanner implementations
  - `QRCodeReceiveActivity.kt`: Android receive flow launcher
  - `QRCodeGenerator.kt`: QR code generation utilities
  - `QrCodeTypeRegistry.kt`: Type detection and routing
  - Multiple QR type checkers implemented
  - Gallery import functionality (Android)

#### Security Considerations
- **Given** QR code operations
- **When** Processing scanned data
- **Then** System must:
  - Validate all input data
  - Verify address checksums
  - Sanitize against injection
  - Handle malformed data gracefully
  - Prevent duplicate transaction scans
  - Implement timeout for transaction QRs

## Platform-Specific Implementation

### 2. Android Platform

#### Android Scanner UI
- **Given** I am on Android device
- **When** Opening QR scanner
- **Then** I should see:
  - ZXing-based camera scanner
  - Custom branded overlay with corner brackets
  - Darkened background around scan area
  - "Receive" button at bottom
  - "Gallery" button for image import
  - Flash toggle for low light
  - Auto-focus enabled

#### Android Permission Handling
- **Given** First scanner use on Android
- **When** Camera permission needed
- **Then** Should:
  - Show permission rationale
  - Request CAMERA permission
  - Handle denial with settings redirect
  - Remember permission state
  - Work without gallery permission

### 3. iOS Platform

#### iOS Scanner UI
- **Given** I am on iOS device
- **When** Opening QR scanner
- **Then** I should see:
  - AVFoundation-based scanner
  - Native iOS camera overlay
  - Custom Core Graphics brackets
  - Clean iOS-style interface
  - Professional appearance
  - Smooth animations

#### iOS Permission Handling
- **Given** First scanner use on iOS
- **When** Camera permission needed
- **Then** Should:
  - Use iOS permission dialog
  - Respect iOS privacy settings
  - Handle denial gracefully
  - Show settings deep link
  - Follow iOS HIG guidelines

### 4. Desktop Platform

#### Desktop QR Handling
- **Given** I am on Desktop
- **When** Using QR features
- **Then** Should support:
  - File-based QR import
  - QR code generation
  - Display for mobile scanning
  - Drag-and-drop support
  - Copy QR image to clipboard

#### Desktop Limitations
- **Given** Desktop platform
- **When** Scanning needed
- **Then** Should:
  - Indicate camera unavailable
  - Suggest file import
  - Support webcam if available
  - Provide alternative input
  - Clear user guidance

## QR Code Type Detection

### 5. Supported QR Code Types

#### Payment Address QR
- **Given** Simple address QR code
- **When** Scanned
- **Then** Should:
  - Detect EVM addresses (0x format)
  - Detect Bitcoin addresses
  - Detect Antelope accounts
  - Route to send screen
  - Pre-fill address field
  - Validate address format

#### Payment Request QR (EIP-681)
- **Given** Payment QR with amount
- **When** Scanned
- **Then** Should:
  - Parse address and amount
  - Support EIP-681 format
  - Extract token type if present
  - Route to send screen
  - Pre-fill address and amount
  - Show token selector if needed

#### WalletConnect QR
- **Given** WalletConnect URI (wc:)
- **When** Scanned
- **Then** Should:
  - Parse WalletConnect v1/v2 URIs
  - Initialize connection flow
  - Show dApp information
  - Request user approval
  - Establish secure session
  - Handle connection errors

#### Transaction Signing QR (Cold Wallet)
- **Given** Unsigned transaction QR
- **When** Scanned in cold wallet
- **Then** Should:
  - Parse transaction data
  - Display transaction details
  - Show gas fees
  - Allow transaction review
  - Enable signing
  - Generate signed QR

#### Sync Account Request QR
- **Given** Account sync QR
- **When** Scanned
- **Then** Should:
  - Parse sync request data
  - Validate request authenticity
  - Show account details
  - Request user confirmation
  - Execute sync process
  - Confirm completion

#### Antelope Import Account QR
- **Given** Antelope account QR
- **When** Scanned
- **Then** Should:
  - Parse account data
  - Validate account format
  - Check account existence
  - Route to import flow
  - Pre-fill account details
  - Handle import process

#### Login/Authentication QR
- **Given** Auth QR code
- **When** Scanned
- **Then** Should:
  - Parse authentication data
  - Validate auth request
  - Show requesting service
  - Request user approval
  - Generate auth response
  - Complete auth flow

#### Anchor Keycert QR
- **Given** Anchor keycert QR
- **When** Scanned
- **Then** Should:
  - Parse certificate data
  - Validate certificate
  - Show certificate details
  - Request user action
  - Process certificate
  - Update wallet state

### 6. Unknown/Invalid QR Handling

#### Unsupported Format
- **Given** Unknown QR type
- **When** Scanned
- **Then** Should:
  - Show error message
  - Indicate unsupported format
  - Not crash or freeze
  - Allow retry
  - Log for debugging

#### Malformed Data
- **Given** Corrupted QR data
- **When** Parsed
- **Then** Should:
  - Detect corruption
  - Show specific error
  - Suggest solutions
  - Allow rescan
  - Maintain stability

## Scanner User Interface

### 7. Scanner Overlay

#### Visual Design
- **Given** Scanner active
- **When** Viewing screen
- **Then** Should display:
  - Semi-transparent overlay
  - White corner brackets
  - Center scan area clear
  - 70% darkness outside area
  - Smooth animations
  - Professional appearance

#### Scan Area
- **Given** QR detection area
- **When** Displayed
- **Then** Should:
  - Be clearly marked
  - Center on screen
  - Size appropriately (250x250dp)
  - Show corner animations
  - Guide user positioning
  - Adapt to screen size

### 8. Scanner Controls

#### Bottom Controls
- **Given** Scanner screen
- **When** Controls displayed
- **Then** Should show:
  - "Receive" button prominently
  - Gallery import option
  - Flash toggle (when available)
  - Cancel/Back navigation
  - Clear button labels
  - Appropriate spacing

#### Control States
- **Given** Control buttons
- **When** User interacts
- **Then** Should:
  - Show pressed states
  - Provide haptic feedback
  - Animate interactions
  - Disable when processing
  - Re-enable after completion

### 9. Gallery Import

#### Image Selection
- **Given** Gallery button tapped
- **When** Picker opens
- **Then** Should:
  - Show system image picker
  - Filter to images only
  - Support recent photos
  - Allow browsing albums
  - Handle large images
  - Support multiple formats

#### QR Detection in Images
- **Given** Image selected
- **When** Processing
- **Then** Should:
  - Show processing indicator
  - Detect QR if present
  - Handle multiple QRs
  - Process first valid QR
  - Show error if no QR
  - Allow retry with new image

## QR Code Display/Receive

### 10. Receive Screen Layout

#### Screen Structure
- **Given** Receive screen opened
- **When** Displayed
- **Then** Should show:
  - QR code centered
  - Address text below
  - Network selector
  - Copy address button
  - Share QR button
  - Save to gallery button
  - Add amount option

#### QR Code Display
- **Given** QR code generation
- **When** Displayed
- **Then** Should:
  - Generate high-res QR (300x300dp)
  - Include error correction
  - Show white background
  - Add padding around code
  - Support dark mode
  - Remain scannable

### 11. Address Management

#### Address Display
- **Given** Wallet address
- **When** Shown below QR
- **Then** Should:
  - Display full address
  - Use monospace font
  - Break appropriately
  - Show network badge
  - Support text selection
  - Enable copy action

#### Copy Functionality
- **Given** Copy button tapped
- **When** Copying address
- **Then** Should:
  - Copy to clipboard
  - Show success toast
  - Include checksum
  - Log action
  - Clear after timeout
  - Handle copy failures

### 12. Network Selection

#### Network Switcher
- **Given** Multi-chain support
- **When** Network selector shown
- **Then** Should:
  - List supported networks
  - Show current network
  - Display network icons
  - Allow switching
  - Update QR on change
  - Remember selection

#### Network-Specific QR
- **Given** Network selected
- **When** QR generated
- **Then** Should:
  - Use correct address format
  - Include network prefix if needed
  - Update immediately
  - Maintain QR quality
  - Show network indicator
  - Validate for network

### 13. Amount Request Feature

#### Add Amount Flow
- **Given** Add amount button
- **When** Tapped
- **Then** Should:
  - Navigate to amount screen
  - Show numeric keypad
  - Support decimals
  - Show currency selector
  - Validate input
  - Update QR with amount

#### Amount Display
- **Given** Amount added
- **When** QR displayed
- **Then** Should:
  - Show amount clearly
  - Include currency symbol
  - Update QR code
  - Show edit option
  - Allow removal
  - Format correctly

#### QR with Amount
- **Given** Payment request QR
- **When** Generated
- **Then** Should:
  - Follow EIP-681 format
  - Include amount parameter
  - Remain scannable
  - Support token amounts
  - Handle decimal precision
  - Validate format

## Sharing and Saving

### 14. Share Functionality

#### Share QR Image
- **Given** Share button tapped
- **When** Sharing initiated
- **Then** Should:
  - Generate image file
  - Open system share sheet
  - Include QR image
  - Add address as text
  - Support all share targets
  - Handle share completion

#### Share Content
- **Given** QR shared
- **When** Content prepared
- **Then** Should include:
  - High-res QR image
  - Wallet address text
  - Network information
  - Amount if specified
  - App attribution
  - Timestamp

### 15. Save to Gallery

#### Save Process
- **Given** Save button tapped
- **When** Saving QR
- **Then** Should:
  - Request storage permission
  - Generate image file
  - Save to gallery/photos
  - Show success message
  - Open in gallery (optional)
  - Handle save failures

#### Saved File
- **Given** QR saved
- **When** File created
- **Then** Should:
  - Use descriptive filename
  - Include timestamp
  - Save as PNG/JPEG
  - Maintain quality
  - Be easily findable
  - Include metadata

## Cold Wallet Integration

### 16. Transaction QR Scanning

#### Unsigned Transaction QR
- **Given** Cold wallet scanning
- **When** Unsigned QR detected
- **Then** Should:
  - Parse transaction data
  - Display sender address
  - Show recipient address
  - Display amount
  - Show gas fees
  - Show network

#### Transaction Review
- **Given** Transaction displayed
- **When** User reviews
- **Then** Should:
  - Show all details clearly
  - Highlight important values
  - Allow scrolling if needed
  - Provide reject option
  - Enable signing
  - Show warnings if needed

### 17. Transaction Signing

#### Signing Process
- **Given** User approves transaction
- **When** Signing initiated
- **Then** Should:
  - Request PIN/biometric
  - Sign with private key
  - Generate signed data
  - Create signed QR
  - Show success indication
  - Log signing event

#### Signed QR Display
- **Given** Transaction signed
- **When** QR displayed
- **Then** Should:
  - Show large QR code
  - Display countdown timer
  - Show "Signed" indicator
  - Prevent screenshots (optional)
  - Auto-refresh if needed
  - Clear after timeout

### 18. QR Expiration

#### Timeout Handling
- **Given** Transaction QR
- **When** Time limit reached
- **Then** Should:
  - Show expiration warning
  - Display countdown
  - Invalidate after timeout
  - Clear QR from screen
  - Require new transaction
  - Log expiration

#### Refresh Options
- **Given** QR expired
- **When** User wants to retry
- **Then** Should:
  - Allow regeneration
  - Maintain transaction data
  - Create new QR
  - Reset timer
  - Show attempt count
  - Limit retries

## Performance and UX

### 19. Scanner Performance

#### Detection Speed
- **Given** QR in view
- **When** Scanning
- **Then** Should:
  - Detect within 2 seconds
  - Work in normal lighting
  - Handle motion blur
  - Support various sizes
  - Work at angles
  - Provide feedback

#### Resource Usage
- **Given** Scanner active
- **When** Running
- **Then** Should:
  - Use < 30% CPU
  - Maintain 30+ FPS
  - Release camera on pause
  - Handle memory efficiently
  - Not drain battery
  - Stop when backgrounded

### 20. User Feedback

#### Scan Success
** **Given** QR scanned successfully
- **When** Detected
- **Then** Should:
  - Provide haptic feedback
  - Show visual confirmation
  - Play sound (optional)
  - Stop scanning
  - Process immediately
  - Navigate smoothly

#### Scan Failure
- **Given** Scan unsuccessful
- **When** Timeout or error
- **Then** Should:
  - Show helpful message
  - Suggest improvements
  - Allow manual input
  - Provide retry option
  - Log failure reason
  - Maintain scanner state

### 21. Focus and Exposure

#### Auto-Focus
- **Given** Camera active
- **When** Scanning
- **Then** Should:
  - Continuously auto-focus
  - Focus on center area
  - Handle focus failures
  - Support tap to focus
  - Maintain focus lock
  - Adjust for distance

#### Exposure Control
- **Given** Various lighting
- **When** Scanning
- **Then** Should:
  - Auto-adjust exposure
  - Handle bright light
  - Work in low light
  - Support flash toggle
  - Maintain visibility
  - Prevent overexposure

## Security Requirements

### 22. Input Validation

#### Address Validation
- **Given** Address scanned
- **When** Processing
- **Then** Must:
  - Verify checksum
  - Validate format
  - Check network compatibility
  - Reject invalid addresses
  - Show specific errors
  - Log validation failures

#### Data Sanitization
- **Given** QR data parsed
- **When** Processing input
- **Then** Must:
  - Sanitize all strings
  - Prevent SQL injection
  - Block script injection
  - Validate data types
  - Limit data size
  - Handle edge cases

### 23. Transaction Security

#### Duplicate Prevention
- **Given** Transaction QR
- **When** Scanned multiple times
- **Then** Must:
  - Detect duplicates
  - Prevent double-spending
  - Show warning
  - Block reprocessing
  - Log attempts
  - Maintain transaction log

#### Signature Verification
- **Given** Signed transaction QR
- **When** Processing
- **Then** Must:
  - Verify signature
  - Check signer authority
  - Validate transaction integrity
  - Reject if tampered
  - Log verification result
  - Show security status

### 24. Privacy Protection

#### Data Handling
- **Given** QR operations
- **When** Processing data
- **Then** Must:
  - Not log sensitive data
  - Clear memory after use
  - Avoid screenshots (sensitive)
  - Encrypt stored QRs
  - Respect privacy settings
  - Minimize data retention

#### Camera Privacy
- **Given** Camera usage
- **When** Scanning
- **Then** Must:
  - Only capture QR area
  - Not store images
  - Release camera quickly
  - Respect permissions
  - Clear buffers
  - Prevent background access

## Accessibility

### 25. Screen Reader Support

#### Scanner Accessibility
- **Given** Screen reader active
- **When** Using scanner
- **Then** Should:
  - Announce scanner state
  - Describe QR when found
  - Read detected content
  - Announce errors clearly
  - Guide positioning
  - Provide audio feedback

#### Receive Screen Accessibility
- **Given** Screen reader active
- **When** On receive screen
- **Then** Should:
  - Describe QR purpose
  - Read address clearly
  - Announce button actions
  - Describe network
  - Read amount if present
  - Confirm actions

### 26. Visual Accessibility

#### High Contrast
- **Given** High contrast mode
- **When** Using QR features
- **Then** Should:
  - Maintain QR scannability
  - Show clear boundaries
  - Use sufficient contrast
  - Highlight active areas
  - Support theme changes
  - Remain functional

#### Font Scaling
- **Given** Large text settings
- **When** Displaying content
- **Then** Should:
  - Scale text appropriately
  - Maintain layout integrity
  - Keep QR code size fixed
  - Wrap text properly
  - Remain usable
  - Support dynamic type

## Error Handling

### 27. Camera Errors

#### Camera Unavailable
- **Given** Camera not accessible
- **When** Opening scanner
- **Then** Should:
  - Show clear error
  - Explain reason
  - Offer alternatives
  - Check permissions
  - Provide settings link
  - Allow manual input

#### Camera Failure
- **Given** Camera malfunction
- **When** During scanning
- **Then** Should:
  - Detect failure
  - Show error message
  - Suggest solutions
  - Allow retry
  - Provide fallback
  - Log error details

### 28. Network Errors

#### Offline Handling
- **Given** No network connection
- **When** Using QR features
- **Then** Should:
  - Work offline when possible
  - Cache necessary data
  - Show offline indicator
  - Queue online operations
  - Sync when connected
  - Inform user clearly

#### API Failures
- **Given** Backend errors
- **When** Processing QR data
- **Then** Should:
  - Show user-friendly error
  - Provide retry option
  - Use cached data if available
  - Log error details
  - Fallback gracefully
  - Maintain functionality

## Enhanced Features *[Enhancements]*

### 29. QR Code History

#### Scan History
- **Given** QR scans performed
- **When** Viewing history
- **Then** Could show:
  - Last 10-20 scans
  - Scan timestamp
  - QR type and content
  - Action performed
  - Quick rescan option
  - Clear history option

#### Receive History
- **Given** QR codes generated
- **When** Viewing history
- **Then** Could show:
  - Generated QRs
  - Creation time
  - Usage status
  - Regenerate option
  - Analytics data
  - Export options

### 30. Bulk Operations

#### Multi-QR Scanning
- **Given** Multiple payments needed
- **When** Bulk scanning
- **Then** Could:
  - Queue multiple scans
  - Show scan count
  - Review all before sending
  - Process in batch
  - Show total amounts
  - Optimize gas fees

#### QR Templates
- **Given** Frequent receivers
- **When** Creating templates
- **Then** Could:
  - Save QR templates
  - Name templates
  - Quick access menu
  - Edit templates
  - Share templates
  - Organize by category

### 31. Advanced QR Features

#### Custom Branding
- **Given** Receive QR display
- **When** Generating QR
- **Then** Could add:
  - Custom logo center
  - Brand colors
  - Custom frames
  - Watermarks
  - Style options
  - Export settings

#### NFC Integration
- **Given** NFC available
- **When** Sharing address
- **Then** Could:
  - Use NFC tap-to-share
  - Faster than QR
  - Automatic format detection
  - Security verification
  - Fallback to QR
  - Log NFC usage

### 32. Deep Link Support

#### External QR Links
- **Given** QR in external app
- **When** Opening link
- **Then** Could:
  - Handle deep links
  - Open scanner directly
  - Process QR data
  - Return to source app
  - Support universal links
  - Track referrers

#### QR Code Schemes
- **Given** Custom URL schemes
- **When** Handling QRs
- **Then** Could support:
  - mangala://scan
  - mangala://receive
  - mangala://pay
  - Web links to QRs
  - Social media integration
  - Email QR links

### 33. Animated QR Codes

#### Data Streaming
- **Given** Large data sets
- **When** QR insufficient
- **Then** Could use:
  - Animated QR sequence
  - Frame synchronization
  - Progress indication
  - Error recovery
  - Checksum verification
  - Completion confirmation

#### Dynamic Updates
- **Given** Real-time data
- **When** QR displayed
- **Then** Could:
  - Update QR dynamically
  - Show live amounts
  - Refresh on-chain data
  - Indicate updates
  - Maintain scannability
  - Version control

### 34. External Verification

#### Third-Party Validation
- **Given** Critical operations
- **When** Extra security needed
- **Then** Could:
  - Verify with external service
  - Check address blacklists
  - Validate against scam database
  - Show trust indicators
  - Require additional confirmation
  - Log verification results

#### Blockchain Verification
- **Given** On-chain validation
- **When** Processing QR
- **Then** Could:
  - Check address balance
  - Verify contract status
  - Validate token existence
  - Check transaction status
  - Show on-chain data
  - Cache results

### 35. Offline QR Validation

#### Local Verification
- **Given** No network access
- **When** Scanning QR
- **Then** Could:
  - Validate format locally
  - Check checksums offline
  - Use cached whitelist
  - Store pending operations
  - Sync when online
  - Show offline status

#### Offline Transaction Queue
- **Given** Transactions while offline
- **When** Scanning payment QRs
- **Then** Could:
  - Queue transactions
  - Sign offline
  - Store securely
  - Broadcast when online
  - Show queue status
  - Handle conflicts

## Testing Scenarios

### 36. Happy Path - Payment QR

1. Open scanner from wallet
2. Point at payment QR code
3. QR detected within 2 seconds
4. Navigate to send screen
5. Address and amount pre-filled
6. Complete transaction
7. Return to wallet

### 37. Happy Path - Receive Flow

1. Tap receive button
2. QR code generates instantly
3. Select network if needed
4. Add amount (optional)
5. Share or save QR
6. Sender scans successfully
7. Receive confirmation

### 38. Edge Cases - Scanner

#### Poor Conditions
- Low light: Flash toggle works
- Blurry QR: Shows positioning guide
- Small QR: Zoom gesture support
- Damaged QR: Error recovery
- Multiple QRs: Selects centered one

#### State Management
- Background/foreground: Maintains state
- Rotation: Handles gracefully
- Permission changes: Responds appropriately
- Memory pressure: Releases resources
- Battery saver: Adjusts performance

### 39. Security Testing

#### Malicious QR Tests
- Script injection: Blocked
- SQL injection: Sanitized
- Buffer overflow: Prevented
- Invalid formats: Handled
- Phishing addresses: Warned

#### Transaction Security Tests
- Duplicate scans: Prevented
- Expired QRs: Rejected
- Tampered data: Detected
- Wrong network: Blocked
- Invalid signatures: Rejected

## Performance Requirements

### 40. Response Times

- **QR detection**: < 2 seconds (good conditions)
- **QR generation**: < 100ms
- **Screen transitions**: < 300ms
- **Gallery import**: < 3 seconds
- **Share preparation**: < 1 second

### 41. Resource Limits

- **Camera CPU usage**: < 30%
- **Memory footprint**: < 50MB
- **Battery drain**: < 5% per 5 minutes
- **Storage for history**: < 10MB
- **Network bandwidth**: Minimal

## Acceptance Criteria Summary

### Must Have (Core Functionality)
✅ QR code scanning with camera
✅ Multiple QR type detection
✅ QR code generation for receive
✅ Address display and copy
✅ Network selection
✅ Amount request feature
✅ Share and save functionality
✅ Gallery import (Android)
✅ Cold wallet transaction QRs
✅ Security validations

### Should Have (Important Enhancements)
⭐ QR scan history
⭐ Flash toggle for low light
⭐ Haptic feedback on scan
⭐ Deep link support
⭐ NFC tap-to-share
⭐ QR templates for frequent use
⭐ Bulk scanning capability
⭐ Custom QR branding
⭐ Offline validation
⭐ Enhanced error messages

### Nice to Have (Future Enhancements)
💡 Animated QR codes
💡 External verification services
💡 Blockchain validation
💡 Multi-device sync
💡 Advanced analytics
💡 AI-powered QR enhancement
💡 Voice-guided scanning
💡 AR overlay information
💡 Social QR sharing
💡 QR code marketplace