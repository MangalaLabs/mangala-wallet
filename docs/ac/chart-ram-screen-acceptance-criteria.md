# Chart RAM Screen - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Chart RAM Screen in the Mangala Wallet application. This screen displays RAM price charts as a bottom sheet, providing users with visual market data including candlestick charts, price trends, and time interval selections for analyzing RAM price movements on Antelope blockchains.

## Screen Purpose

### 1. RAM Price Visualization

#### Primary Function
- **Given** User needs market analysis
- **When** Viewing RAM price data
- **Then** Should provide:
  - Candlestick chart display
  - Multiple time intervals
  - Price change indicators
  - Current price display
  - 24h percentage change
  - Interactive chart features

#### Chart Types
- **Given** Market data visualization
- **When** Displayed
- **Then** Shows:
  - OHLC (Open, High, Low, Close) candles
  - Time-based intervals
  - Price axis formatting
  - Date/time axis labels
  - Color-coded movements
  - Grid lines for reference

## Screen Parameters

### 2. Initialization Parameters

#### Required Parameters
- **Given** Screen initialization
- **When** Opening chart
- **Then** Must provide:
  - isLoading: Boolean (loading state)
  - ramPrice: String (current price)
  - ramCurrency: String (currency symbol)
  - pnlPercent: String (profit/loss percentage)
  - pnlColor: Color (red/green indicator)

#### Parameter Usage
- **Given** Parameters provided
- **When** Screen loads
- **Then** Should:
  - Display loading placeholders
  - Show current price
  - Format currency properly
  - Apply PnL color coding
  - Track analytics event

## Visual Design

### 3. Bottom Sheet Layout

#### Sheet Structure
- **Given** Bottom sheet display
- **When** Rendered
- **Then** Should show:
  - Theme background color
  - Navigation bar padding
  - Default horizontal padding
  - Scrollable content
  - Smooth sheet animations

#### Content Organization
- **Given** Sheet content
- **When** Displayed
- **Then** Should include:
  - Header section with price
  - Close button (X icon)
  - Chart visualization area
  - Time interval buttons
  - Proper spacing between sections

## Header Section

### 4. Price Display

#### Current Price
- **Given** RAM price data
- **When** Displayed
- **Then** Should show:
  - Price truncated to 5 decimals
  - Currency symbol appended
  - Regular font size
  - Medium font weight
  - Secondary text color
  - Loading placeholder support

#### Price Change Indicator
- **Given** PnL percentage
- **When** Shown
- **Then** Should display:
  - Percentage value
  - Color coding (green/red)
  - Tiny font size
  - Normal font weight
  - Dynamic color from pnlColor
  - Loading state handling

### 5. Close Button

#### Button Design
- **Given** Dismiss action
- **When** Displayed
- **Then** Should show:
  - X button icon (top right)
  - Standard icon button size
  - Full width clickable area
  - Loading placeholder
  - Touch feedback
  - Accessibility support

#### Button Action
- **Given** Close button tapped
- **When** Triggered
- **Then** Should:
  - Hide bottom sheet
  - Smooth animation
  - Return to parent
  - Maintain parent state
  - Clean up resources
  - Track dismissal

## Chart Display

### 6. Market Chart Component

#### Chart Configuration
- **Given** Chart component
- **When** Rendered
- **Then** Should have:
  - Full width layout
  - 300dp fixed height
  - Horizontal padding (default)
  - Candlestick visualization
  - Interactive features
  - Smooth rendering

#### Chart Colors
- **Given** Theme integration
- **When** Applied
- **Then** Should use:
  - Background: theme bg color
  - Positive: green color
  - Negative: coral color
  - Text: primary text color
  - Lines: border color
  - Grid: subtle lines

### 7. Chart Data

#### Candle Display
- **Given** OHLC data
- **When** Visualized
- **Then** Should show:
  - Open price
  - High price
  - Low price
  - Close price
  - Volume (if available)
  - Time stamps

#### Data Formatting
- **Given** Price values
- **When** Displayed
- **Then** Should:
  - Format to 4 decimals (0.0000)
  - Use DecimalFormat
  - Apply consistent precision
  - Handle large numbers
  - Show abbreviated values
  - Maintain readability

### 8. Time Axis

#### Time Labels
- **Given** Chart time mode
- **When** Displayed
- **Then** Should show:
  - TIME_ONLY: time format only
  - DATE_TIME: time + date on new line
  - DATE_ONLY: date format only
  - Current timezone used
  - Localized formatting
  - Appropriate intervals

#### Label Modes
- **Given** ChartTimeLabelMode
- **When** Applied
- **Then** Determines:
  - Label format selection
  - Line break handling
  - Space optimization
  - Readability balance
  - Axis crowding prevention
  - Dynamic adjustment

## Time Interval Selection

### 9. ButtonRowTime Component

#### Button Layout
- **Given** Time interval buttons
- **When** Displayed
- **Then** Should show:
  - Horizontal row layout
  - Equal weight distribution
  - Small spacing between
  - Default horizontal padding
  - Fixed height (IconNormalSize)
  - Responsive to selection

#### Available Intervals
- **Given** SamplingInterval options
- **When** Listed
- **Then** Should include:
  - 1H (One hour)
  - 4H (Four hours)
  - 1D (One day)
  - 1W (One week)
  - 1M (One month)
  - All (All time)

### 10. Interval Button States

#### Selected State
- **Given** Interval selected
- **When** Active
- **Then** Should show:
  - bgBadge background color
  - textOnBadge text color
  - bgBadge border color
  - Visual distinction
  - No hover effect
  - Maintained selection

#### Unselected State
- **Given** Interval not selected
- **When** Displayed
- **Then** Should show:
  - Theme bg background
  - Primary text color
  - Border color outline
  - 1dp border width
  - Enabled interaction
  - Hover feedback

#### Disabled State
- **Given** No data available
- **When** Loading
- **Then** Should show:
  - bgInnerCard background
  - Secondary text color
  - Disabled interaction
  - No click response
  - Visual indication
  - Loading state

### 11. Interval Selection

#### Selection Behavior
- **Given** Interval button clicked
- **When** Processing
- **Then** Should:
  - Update selected interval
  - Load new chart data
  - Show loading state
  - Update chart display
  - Maintain scroll position
  - Track selection event

#### Data Loading
- **Given** New interval selected
- **When** Fetching data
- **Then** Should:
  - Call loadData function
  - Pass SamplingInterval
  - Show loading indicators
  - Handle errors gracefully
  - Update on success
  - Cache previous data

## State Management

### 12. Screen States

#### Loading State
- **Given** Data fetching
- **When** In progress
- **Then** Should:
  - Show placeholders
  - Disable interactions
  - Maintain layout
  - Display skeleton UI
  - Smooth transitions
  - Prevent flicker

#### Success State
- **Given** Data loaded
- **When** Displayed
- **Then** Should show:
  - Complete chart
  - All price info
  - Active buttons
  - Updated values
  - Smooth animations
  - Interactive features

#### Error State
- **Given** Loading failed
- **When** Handled
- **Then** Should:
  - Show error message
  - Retry option
  - Maintain layout
  - Log error details
  - Fallback display
  - User guidance

## Data Management

### 13. Chart Data Model

#### ChartRamUiModel
- **Given** UI state
- **When** Structured
- **Then** Contains:
  - OHLC data list
  - Selected interval
  - Chart time label mode
  - Loading states
  - Error information
  - Cached values

#### Price Updates
- **Given** Real-time data
- **When** Available
- **Then** Should:
  - Update current price
  - Refresh percentage
  - Append new candles
  - Maintain history
  - Smooth transitions
  - Prevent jumps

## Enhanced Features *[Enhancements]*

### 14. Interactive Chart Features

#### Touch Interactions
- **Given** User interaction
- **When** Implemented
- **Then** Could support:
  - Pinch to zoom
  - Pan to scroll
  - Tap for details
  - Long press info
  - Crosshair cursor
  - Value tooltips

#### Chart Overlays
- **Given** Technical analysis
- **When** Added
- **Then** Could show:
  - Moving averages
  - Volume bars
  - Support/resistance lines
  - Trend indicators
  - Price targets
  - Annotations

### 15. Advanced Time Intervals

#### Custom Intervals
- **Given** User preferences
- **When** Configured
- **Then** Could offer:
  - 15-minute intervals
  - 30-minute intervals
  - 2-hour intervals
  - 3-day intervals
  - Custom ranges
  - Date picker

#### Interval Memory
- **Given** User selection
- **When** Remembered
- **Then** Could:
  - Save last selection
  - Default to preference
  - Quick switch options
  - Favorites list
  - Recent intervals
  - Sync across screens

### 16. Chart Export

#### Image Export
- **Given** Chart displayed
- **When** Exporting
- **Then** Could:
  - Save as image
  - Copy to clipboard
  - Share functionality
  - Include watermark
  - Custom resolution
  - Format options

#### Data Export
- **Given** Chart data
- **When** Requested
- **Then** Could export:
  - CSV format
  - JSON format
  - Excel compatible
  - Time range selection
  - Filtered data
  - API integration

### 17. Price Alerts

#### Alert Configuration
- **Given** Price monitoring
- **When** Setting alerts
- **Then** Could:
  - Set price targets
  - Percentage changes
  - Volume thresholds
  - Multiple alerts
  - Notification types
  - Alert history

### 18. Comparison Features

#### Multi-chart View
- **Given** Comparison needs
- **When** Enabled
- **Then** Could show:
  - Multiple timeframes
  - Side-by-side charts
  - Overlay comparisons
  - Different metrics
  - Synchronized scrolling
  - Relative performance

### 19. Chart Themes

#### Theme Options
- **Given** Customization
- **When** Available
- **Then** Could offer:
  - Light/dark modes
  - Color schemes
  - Candle styles
  - Grid options
  - Font sizes
  - Layout preferences

### 20. Performance Indicators

#### Technical Indicators
- **Given** Analysis tools
- **When** Added
- **Then** Could include:
  - RSI indicator
  - MACD lines
  - Bollinger Bands
  - Fibonacci retracement
  - Stochastic oscillator
  - Custom indicators

## Accessibility

### 21. Screen Reader Support

#### Chart Accessibility
- **Given** Screen reader active
- **When** Navigating
- **Then** Should announce:
  - Current price
  - Percentage change
  - Selected interval
  - Chart trends
  - Button states
  - Data points

#### Alternative Text
- **Given** Visual elements
- **When** Described
- **Then** Should provide:
  - Chart description
  - Trend summary
  - Price movements
  - Time range
  - Key statistics
  - Navigation hints

### 22. Keyboard Navigation

#### Focus Management
- **Given** Keyboard control
- **When** Navigating
- **Then** Should:
  - Tab through buttons
  - Select intervals
  - Close with Escape
  - Arrow key navigation
  - Enter to select
  - Clear focus states

## Performance

### 23. Rendering Performance

- **Chart render**: < 100ms
- **Data update**: < 200ms
- **Interval switch**: < 300ms
- **Smooth scrolling**: 60 FPS
- **Animation frame rate**: 60 FPS
- **Memory usage**: < 20MB

### 24. Data Management

- **Cache size**: < 5MB
- **History limit**: 1000 candles
- **Update frequency**: As needed
- **Network efficiency**: Batch requests
- **CPU usage**: < 10%
- **Battery impact**: Minimal

## Testing Scenarios

### 25. Happy Path - View Chart

1. Open RAM details screen
2. Tap chart icon
3. Bottom sheet slides up
4. Chart loads with 1H default
5. View price and percentage
6. Select different interval
7. Chart updates smoothly
8. Close with X button
9. Sheet dismisses
10. Return to parent

### 26. Interval Selection Path

1. Chart displayed
2. Current interval highlighted
3. Tap different interval
4. Button state changes
5. Loading indicator shown
6. New data loads
7. Chart updates
8. Selection maintained
9. Smooth transition

### 27. Edge Cases

#### Data Edge Cases
- No data available: Show message
- Partial data: Display available
- Network failure: Show cached
- Invalid response: Error state
- Empty intervals: Disable buttons

#### Interaction Edge Cases
- Rapid interval switching: Debounce
- Multiple taps: Prevent duplicates
- Sheet drag: Handle properly
- Background tap: Consider dismissal
- Rotation: Maintain state

## Analytics

### 28. Event Tracking

#### Chart Events
- **Given** Analytics enabled
- **When** User interacts
- **Then** Should track:
  - Screen view: ANTELOPE_RAM_CHARTS
  - Interval selections
  - View duration
  - Dismissal method
  - Error occurrences
  - Data load times

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ Bottom sheet presentation
✅ Price display with 5 decimal truncation
✅ Percentage change with color coding
✅ X button for dismissal
✅ 300dp height chart
✅ Candlestick visualization
✅ Time interval selection buttons
✅ 6 standard intervals (1H, 4H, 1D, 1W, 1M, All)
✅ Selected state highlighting
✅ Time/date formatting options

### Should Have (Reasonable Enhancements)
⭐ Interactive chart features (zoom, pan)
⭐ Touch interactions for price details
⭐ Chart caching for performance
⭐ Error state handling
⭐ Loading skeleton UI
⭐ Smooth animations
⭐ Keyboard navigation
⭐ Export functionality
⭐ Additional time intervals
⭐ Volume display

### Nice to Have (Future Enhancements)
💡 Technical indicators
💡 Drawing tools
💡 Price alerts
💡 Comparison charts
💡 Custom themes
💡 Multi-chart layouts
💡 Advanced analysis tools
💡 Social sharing
💡 Historical data export
💡 API integration