# NET and CPU Screen - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the NET and CPU Screen in the Mangala Wallet application. This screen provides options for managing blockchain resources (NET and CPU) on Antelope networks, offering multiple methods to acquire resources including PowerUp, REX rental, and staking mechanisms.

## Screen Purpose

### 1. Resource Management Hub

#### Primary Function
- **Given** User needs blockchain resources
- **When** On Antelope network
- **Then** Can access:
  - PowerUp for temporary resources
  - REX rental system
  - Staking options (dev only)
  - Unstaking options (dev only)
  - Cost comparisons

#### Resource Types
- **Given** Antelope blockchain requirements
- **When** Managing resources
- **Then** Supports:
  - CPU (processing power)
  - NET (network bandwidth)
  - Same interface for both
  - Different pricing for each
  - Contextual information

## Screen Parameters

### 2. Initialization Parameters

#### Required Parameters
- **Given** Screen initialization
- **When** Navigating to screen
- **Then** Must provide:
  - accountName: String (Antelope account)
  - isCPU: Boolean (true for CPU, false for NET)

#### Parameter Usage
- **Given** Parameters provided
- **When** Screen loads
- **Then** Should:
  - Display correct resource type
  - Load appropriate rates
  - Show relevant descriptions
  - Pass to child screens
  - Track analytics correctly

## Visual Design

### 3. Screen Layout

#### Overall Structure
- **Given** Screen displays
- **When** Rendered
- **Then** Should show:
  - Centered top bar with title
  - Introduction section
  - Scrollable content area
  - Resource provider cards
  - Safe area padding
  - Background color (bg)

#### Content Arrangement
- **Given** Content area
- **When** Displayed
- **Then** Should have:
  - 46dp top spacing
  - Title and description
  - Card stack layout
  - 16dp spacing between cards
  - Default horizontal padding

### 4. Top Navigation Bar

#### Navigation Elements
- **Given** Top bar
- **When** Displayed
- **Then** Should show:
  - Back button (left)
  - Centered title (CPU or NET)
  - Consistent styling
  - Proper touch targets
  - Navigation functionality

#### Title Display
- **Given** Resource type
- **When** Title shown
- **Then** Should display:
  - "CPU" for CPU resources
  - "NET" for NET resources
  - Localized strings
  - Center alignment
  - Proper font weight

## Introduction Section

### 5. Resource Introduction

#### Title Text
- **Given** Introduction section
- **When** Displayed
- **Then** Should show:
  - "What is [CPU/NET]?" title
  - Medium font weight
  - Primary text color
  - Localized content
  - Dynamic resource name

#### Description Text
- **Given** Introduction description
- **When** Displayed
- **Then** Should explain:
  - Resource purpose
  - Why it's needed
  - Basic concepts
  - Secondary text color
  - Clear language

### 6. Educational Content *[Enhancement]*

#### Learn More Section
- **Given** User needs education
- **When** Viewing screen
- **Then** Could provide:
  - Expandable FAQ section
  - Resource usage examples
  - Best practices guide
  - Video tutorials
  - External documentation links

## Resource Provider Cards

### 7. Card Layout

#### Card Design
- **Given** Provider card
- **When** Displayed
- **Then** Should have:
  - Rounded corners (12dp)
  - 1dp border (border color)
  - Inner card background
  - 24dp horizontal padding
  - Center alignment

#### Card Content Structure
- **Given** Card information
- **When** Arranged
- **Then** Should show:
  - Provider title
  - Cost display (large)
  - Unit description
  - Service description
  - Action button
  - Proper spacing

### 8. PowerUp Card

#### PowerUp Display
- **Given** PowerUp option
- **When** Displayed
- **Then** Should show:
  - Title: "PowerUp"
  - Current rate (formatted)
  - Unit: "EOS/ms/24h" or "EOS/kb/24h"
  - Description of service
  - "Power Up" button

#### PowerUp Features
- **Given** PowerUp service
- **When** Described
- **Then** Should explain:
  - 24-hour rental period
  - No permanent stake
  - Instant availability
  - Pay-per-use model
  - Cost efficiency

### 9. REX Card

#### REX Display
- **Given** REX option
- **When** Displayed
- **Then** Should show:
  - Title: "REX"
  - Current REX rate
  - Same unit format
  - REX description
  - "Rent via REX" button

#### REX Features
- **Given** REX service
- **When** Described
- **Then** Should explain:
  - 30-day rental period
  - REX token system
  - Market-based pricing
  - Rental advantages
  - How REX works

### 10. Staking Card *[Development Only]*

#### Staking Display
- **Given** Development environment
- **When** Staking shown
- **Then** Should display:
  - Title: "Staking"
  - Staking rate
  - Resource unit
  - Staking description
  - "Stake" button

#### Staking Features
- **Given** Staking option
- **When** Available
- **Then** Should explain:
  - Permanent allocation
  - Self-staking benefits
  - 3-day unstaking period
  - Resource ownership
  - Cost implications

### 11. Unstaking Card *[Development Only]*

#### Unstaking Display
- **Given** Development environment
- **When** Unstaking shown
- **Then** Should display:
  - Title: "Unstaking"
  - Current rate (same as staking)
  - Resource unit
  - Unstaking description
  - "Unstake" button

#### Unstaking Features
- **Given** Unstaking option
- **When** Available
- **Then** Should explain:
  - Resource release
  - 3-day waiting period
  - Funds recovery
  - Process steps
  - Implications

## Cost Display

### 12. Rate Formatting

#### Price Display
- **Given** Resource rates
- **When** Displayed
- **Then** Should show:
  - 4 decimal places
  - Native token symbol
  - Large font (32sp)
  - Semi-bold weight
  - Loading placeholder if null

#### Unit Display
- **Given** Measurement units
- **When** Shown
- **Then** Should display:
  - CPU: "EOS/ms/24h" format
  - NET: "EOS/kb/24h" format
  - Secondary text color
  - Smaller font size
  - Below price

### 13. Loading States

#### Rate Loading
- **Given** Rates being fetched
- **When** Loading
- **Then** Should show:
  - "0.0000" placeholder
  - Skeleton animation
  - Maintain layout
  - Smooth transition
  - No layout shift

#### Error Handling *[Enhancement]*
- **Given** Rate fetch fails
- **When** Error occurs
- **Then** Could show:
  - Error message
  - Retry option
  - Cached rates if available
  - Fallback values
  - User guidance

## Navigation Actions

### 14. PowerUp Navigation

#### PowerUp Button Action
- **Given** PowerUp button tapped
- **When** Navigating
- **Then** Should:
  - Navigate to PowerUpScreen
  - Pass account name
  - Pass isCPU parameter
  - Maintain back stack
  - Track analytics

### 15. REX Navigation

#### REX Button Action
- **Given** REX button tapped
- **When** Navigating
- **Then** Should:
  - Navigate to RentViaRexScreen
  - Pass account name
  - Pass isCPU parameter
  - Proper screen transition
  - State preservation

### 16. Staking Navigation

#### Stake Button Action
- **Given** Stake button tapped
- **When** Navigating
- **Then** Should:
  - Navigate to StakeForResourceScreen
  - Pass isStakeRex: true
  - Pass account and CPU params
  - Development only
  - Proper transition

#### Unstake Button Action
- **Given** Unstake button tapped
- **When** Navigating
- **Then** Should:
  - Navigate to StakeForResourceScreen
  - Pass isStakeRex: false
  - Pass parameters
  - Development only
  - Handle navigation

## Dynamic Content

### 17. Resource-Specific Content

#### CPU-Specific Display
- **Given** isCPU is true
- **When** Displaying content
- **Then** Should show:
  - CPU-related titles
  - Processing explanations
  - ms (millisecond) units
  - CPU usage examples
  - CPU-specific rates

#### NET-Specific Display
- **Given** isCPU is false
- **When** Displaying content
- **Then** Should show:
  - NET-related titles
  - Bandwidth explanations
  - kb (kilobyte) units
  - NET usage examples
  - NET-specific rates

### 18. Localization

#### Localized Strings
- **Given** Different languages
- **When** Text displayed
- **Then** Should use:
  - Localized resource names
  - Translated descriptions
  - Formatted strings
  - Proper placeholders
  - Cultural adaptations

## Environment Configuration

### 19. Development Features

#### Dev Environment Detection
- **Given** Build environment
- **When** Checking configuration
- **Then** Should:
  - Use BuildEnvironmentProvider
  - Check isDevelopmentEnvironment()
  - Show/hide staking cards
  - Control feature visibility
  - Maintain production safety

#### Production Environment
- **Given** Production build
- **When** Running
- **Then** Should:
  - Hide staking cards
  - Show only PowerUp and REX
  - Stable feature set
  - No experimental options
  - Production-ready UI

## Enhanced Features *[Enhancements]*

### 20. Cost Comparison

#### Comparison View
- **Given** Multiple options
- **When** Viewing costs
- **Then** Could show:
  - Side-by-side comparison
  - Cost calculator
  - Break-even analysis
  - Recommendation engine
  - Historical pricing

#### Cost Breakdown
- **Given** Rate information
- **When** Enhanced display
- **Then** Could show:
  - Per-day costs
  - Monthly projections
  - Usage-based estimates
  - Fee breakdown
  - Total cost of ownership

### 21. Resource Calculator

#### Usage Estimator
- **Given** Resource needs
- **When** Planning
- **Then** Could provide:
  - Action cost estimates
  - Required resources
  - Optimal allocation
  - Usage patterns
  - Recommendations

#### Budget Planner
- **Given** Budget constraints
- **When** Planning resources
- **Then** Could show:
  - Budget allocation
  - Resource mix optimization
  - Cost projections
  - Savings opportunities
  - Alert thresholds

### 22. Quick Actions

#### Favorite Methods
- **Given** User preferences
- **When** Frequently used
- **Then** Could:
  - Save preferred method
  - Quick repeat actions
  - One-tap purchase
  - Default selections
  - Usage history

#### Bulk Operations
- **Given** Multiple resources
- **When** Managing
- **Then** Could support:
  - CPU + NET bundle
  - Batch operations
  - Combined purchases
  - Package deals
  - Efficient workflows

### 23. Resource Monitoring

#### Current Usage Display
- **Given** Active resources
- **When** On screen
- **Then** Could show:
  - Current CPU/NET levels
  - Usage percentage
  - Remaining resources
  - Expiry countdowns
  - Usage graphs

#### Alert System
- **Given** Resource levels
- **When** Monitoring
- **Then** Could provide:
  - Low resource warnings
  - Expiry notifications
  - Price change alerts
  - Usage spike detection
  - Proactive suggestions

### 24. Historical Data

#### Price History
- **Given** Rate tracking
- **When** Available
- **Then** Could show:
  - Price charts
  - Trend analysis
  - Best time to buy
  - Historical comparisons
  - Market insights

#### Usage History
- **Given** Account history
- **When** Tracked
- **Then** Could display:
  - Past transactions
  - Resource consumption
  - Cost analysis
  - Usage patterns
  - Optimization suggestions

### 25. Educational Features

#### Interactive Tutorials
- **Given** New users
- **When** Learning
- **Then** Could provide:
  - Step-by-step guides
  - Interactive demos
  - Tooltips on hover
  - Contextual help
  - Best practices

#### Resource Guide
- **Given** Information needs
- **When** Accessing help
- **Then** Could show:
  - What uses CPU/NET
  - Common scenarios
  - Troubleshooting
  - FAQ section
  - Video tutorials

## Accessibility

### 26. Screen Reader Support

#### Content Announcements
- **Given** Screen reader active
- **When** Navigating
- **Then** Should announce:
  - Resource type (CPU/NET)
  - Current rates
  - Card contents
  - Button purposes
  - Navigation feedback

#### Semantic Structure
- **Given** Accessibility needs
- **When** Structured
- **Then** Should have:
  - Proper headings
  - Descriptive labels
  - Focus order
  - Landmark regions
  - Clear hierarchy

### 27. Visual Accessibility

#### High Contrast Mode
- **Given** High contrast enabled
- **When** Viewing
- **Then** Should provide:
  - Enhanced borders
  - Clear text contrast
  - Visible focus states
  - Distinct cards
  - Readable rates

#### Text Scaling
- **Given** Large text enabled
- **When** Applied
- **Then** Should:
  - Scale appropriately
  - Maintain layout
  - Wrap descriptions
  - Preserve functionality
  - Stay readable

## Performance Requirements

### 28. Loading Performance

- **Screen load**: < 200ms
- **Rate fetch**: < 1 second
- **Navigation**: < 100ms
- **Scroll performance**: 60 FPS
- **Card rendering**: < 50ms

### 29. Data Management

- **Rate caching**: 5 minutes
- **Auto-refresh**: Optional
- **Background updates**: Supported
- **State persistence**: On navigation
- **Memory usage**: < 20MB

## Testing Scenarios

### 30. Happy Path - View Resources

1. Navigate to screen
2. See introduction text
3. View PowerUp card with rate
4. View REX card with rate
5. Tap PowerUp button
6. Navigate to PowerUp screen
7. Complete resource purchase

### 31. Happy Path - Compare Options

1. Open NET resource screen
2. Read descriptions
3. Compare PowerUp vs REX
4. Check current rates
5. Make informed decision
6. Navigate to chosen option
7. Successfully acquire resources

### 32. Edge Cases

#### Data Edge Cases
- No rates available: Show placeholders
- Network error: Show cached/error state
- Invalid account: Handle gracefully
- Zero rates: Display appropriately
- Timeout: Retry mechanism

#### Navigation Edge Cases
- Quick back/forward: State preserved
- Deep link: Proper initialization
- Screen rotation: Maintain state
- Background/foreground: Refresh data
- Multiple taps: Prevent duplicates

## Security Requirements

### 33. Data Security

- **Rate integrity**: Verify sources
- **Account validation**: Check ownership
- **Secure navigation**: Parameter validation
- **No sensitive data**: In analytics
- **API security**: Authenticated calls

## Analytics

### 34. Event Tracking

#### Screen Events
- **Given** Analytics enabled
- **When** User interacts
- **Then** Should track:
  - Screen view (CPU/NET specific)
  - Card selections
  - Navigation actions
  - Rate views
  - Time on screen

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ CPU and NET resource screens
✅ PowerUp card with rates
✅ REX card with rates
✅ Navigation to respective screens
✅ Dynamic content based on resource type
✅ Localized strings
✅ Development-only staking cards
✅ Proper screen layout
✅ Loading placeholders
✅ Back navigation

### Should Have (Reasonable Enhancements)
⭐ Cost comparison view
⭐ Resource calculator
⭐ Current usage display
⭐ Price history charts
⭐ Error state handling
⭐ Refresh capability
⭐ Help/FAQ section
⭐ Usage recommendations
⭐ Bundle purchases
⭐ Better loading states

### Nice to Have (Future Enhancements)
💡 Resource monitoring dashboard
💡 Alert system
💡 Predictive analytics
💡 Auto-purchase options
💡 Market analysis
💡 Interactive tutorials
💡 Voice input support
💡 Widget support
💡 Push notifications
💡 AI-powered recommendations