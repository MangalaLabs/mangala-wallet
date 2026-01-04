# Vaulta Resources Screen - Pure Mobile RPG Design Documentation

## Project Overview

### Product Vision
Vaulta is a crypto wallet for the EOS/Vaulta blockchain that transforms complex resource management into an intuitive Mobile RPG experience. The Resources screen is the core interface where users manage CPU, NET, and RAM - the three essential resources needed for blockchain transactions.

### Design Philosophy
- **Target Audience**: GenZ users (18-25) familiar with mobile RPG games
- **Design Language**: Pure Mobile RPG inspired by Genshin Impact, Honkai Star Rail
- **Core Principle**: Make blockchain resources feel like game equipment/stats

## Technical Architecture

### Technology Stack
- **Frontend Framework**: React Native (recommended) or Flutter
- **State Management**: Redux/MobX for resource states
- **Blockchain Integration**: Vaulta native SDK
- **Animation Library**: Lottie for complex animations
- **UI Components**: Custom components with RPG theming

### Screen Structure

```
VaultaResourcesScreen/
├── Header/
│   ├── PlayerInfo (Avatar, Name, Wallet)
│   └── CurrencyDisplay (EOS Balance)
├── ResourcesPanel/
│   ├── PanelHeader (Title, Power Level)
│   └── ResourceCards/
│       ├── CPUCard (Epic tier)
│       ├── NETCard (Epic tier)
│       └── RAMCard (Legendary tier)
└── SkillBar/
    ├── BoostButton
    ├── TradeButton
    └── AutoOptimizeButton
```

## Component Specifications

### 1. Header Component

**PlayerInfo Section**
- **Avatar**: 56x56px with animated gradient border
    - Rotation animation: 3s linear infinite
    - Glow effect on user interaction
- **Player Name**: Customizable display name
- **Wallet Address**: Truncated format (vault...4k2j)

**Currency Display**
- **Icon**: Gold coin emoji or custom asset
- **Amount**: Real-time EOS balance
- **Format**: Comma-separated thousands (1,337)
- **Update**: WebSocket for live updates

### 2. Resource Cards

Each resource is displayed as an RPG equipment card with rarity tiers:

**Card Structure**
```typescript
interface ResourceCard {
  id: 'cpu' | 'net' | 'ram';
  name: string;          // Display name (e.g., "Lightning Core")
  type: string;          // Resource type description
  rarity: 'epic' | 'legendary';
  icon: string;          // Emoji or SVG path
  current: number;       // Current amount
  max: number;          // Maximum capacity
  unit: string;         // μs, KB, etc.
  percentage: number;    // Usage percentage
  regenTime?: string;    // For CPU/NET only
  price?: number;        // For RAM only
  priceChange?: number;  // 24h change for RAM
}
```

**Visual States**
- **Idle**: Subtle breathing animation
- **Hover/Touch**: Lift effect + glow intensifies
- **Active**: Pulse animation during transactions
- **Critical**: Red glow + warning animation when <20%

### 3. Resource Bar Visualization

**Progress Bar Specs**
- Height: 24px
- Border radius: 12px
- Fill animation: 300ms ease
- Text overlay: Current/Max values

**Color Schemes**
- **CPU**: Blue gradient (#3b82f6 → #60a5fa)
- **NET**: Green gradient (#10b981 → #34d399)
- **RAM**: Gold gradient (#fbbf24 → #fcd34d)

### 4. Action Buttons

**Per Resource Actions**
- **CPU/NET**: Recharge, Upgrade
- **RAM**: Buy, Sell, Market

**Button States**
- Default: rgba(255, 255, 255, 0.05) background
- Hover: 10% opacity increase + translateY(-1px)
- Active: Scale(0.98)
- Primary: Purple tint for main actions

### 5. Skill Bar (Bottom Navigation)

**Fixed Position Bar**
- 3 main action buttons: Boost, Trade, Auto
- Size: 72x72px per button
- Hover: Rotating glow border animation
- Active: Pulse + scale effect

## Interaction Flows

### 1. Resource Boosting Flow
```
User taps Boost → Modal appears → Select boost preset:
- Gaming Mode: +5k CPU, +500KB NET
- Trading Mode: +10k CPU, +100KB NET
- Eco Mode: Minimum viable
- Max Power: 50% of available EOS
→ Preview changes → Confirm → Animation → Update UI
```

### 2. RAM Trading Flow
```
User taps Trade → Market interface slides up →
Shows: Current price, 24h chart, calculator →
Input amount → Preview cost/return →
Confirm → Transaction → Success animation
```

### 3. Auto-Optimize Flow
```
User taps Auto → AI analyzes usage patterns →
Suggests optimal allocation → Preview →
One-tap confirm → Batch transaction
```

## API Integration

### Required Vaulta APIs

```typescript
// Resource Status
vaulta_getAccount(account: string) // Get all resources
vaulta_getResourcePrices() // RAM market price

// Resource Management
vaulta_prepareTransfer() // For EOS transfers
vaulta_buyRam(payer, receiver, quant)
vaulta_sellRam(account, bytes)
vaulta_delegateBandwidth(from, receiver, cpu, net)
vaulta_undelegateBandwidth(from, receiver, cpu, net)

// Real-time Updates
subscribeToAccount(account) // WebSocket for live data
subscribeToPriceFeeds() // RAM price updates
```

### Data Refresh Strategy
- Account data: Every 3 seconds
- RAM price: Real-time WebSocket
- Resource regeneration: Calculate client-side
- Transaction status: Immediate + polling

## Animation Specifications

### Micro-animations
1. **Resource Regeneration**: Filling animation with particles
2. **Transaction Success**: Burst effect + number float up
3. **Low Resource Warning**: Pulse + glow effect
4. **Price Changes**: Number morphing animation

### Transition Animations
- Screen entry: Fade in + slide up (300ms)
- Card interactions: Spring physics
- Modal appearances: Backdrop blur + scale
- Success states: Confetti particles

## State Management

```typescript
interface ResourcesState {
  account: {
    name: string;
    balance: number;
    powerLevel: number;
  };
  resources: {
    cpu: ResourceData;
    net: ResourceData;
    ram: ResourceData & { marketPrice: number };
  };
  ui: {
    isLoading: boolean;
    activeModal: 'boost' | 'trade' | 'stake' | null;
    notifications: Notification[];
  };
}
```

## Error Handling

### Common Scenarios
1. **Insufficient Resources**: Show specific amount needed
2. **Network Issues**: Retry with exponential backoff
3. **Transaction Failures**: Clear error messages + recovery options
4. **Price Volatility**: Warning when RAM price changes >10%

### User-Friendly Messages
- ❌ "Error: Insufficient CPU"
- ✅ "Need 500 μs more CPU. Boost for 0.5 EOS?"

## Performance Optimization

### Critical Metrics
- Initial load: <2 seconds
- Interaction response: <100ms
- Animation FPS: 60fps target
- Memory usage: <50MB

### Optimization Strategies
1. Lazy load heavy animations
2. Virtualize long lists
3. Cache resource calculations
4. Debounce API calls
5. Use React.memo for cards

## Accessibility

### Requirements
- Screen reader support for all elements
- Keyboard navigation for web version
- Color contrast ratio: 4.5:1 minimum
- Touch targets: 44x44px minimum
- Haptic feedback for actions

## Testing Strategy

### Unit Tests
- Resource calculation logic
- Price formatting functions
- State mutations

### Integration Tests
- API call sequences
- Transaction flows
- Error scenarios

### E2E Tests
- Complete user journeys
- Performance benchmarks
- Cross-platform compatibility

## Deployment Considerations

### Mobile (React Native)
```json
{
  "ios": {
    "minimumVersion": "13.0",
    "requiredPermissions": ["haptics", "notifications"]
  },
  "android": {
    "minSdkVersion": 21,
    "permissions": ["VIBRATE", "INTERNET"]
  }
}
```

### Web Fallback
- Progressive enhancement
- Touch-first design
- Responsive breakpoints: 320px, 428px, 768px

## Future Enhancements

### Phase 2 Features
1. **Social Features**: Compare resources with friends
2. **Achievements**: "First RAM Trade", "Power User"
3. **Daily Missions**: Gamified engagement
4. **Resource History**: Charts and analytics
5. **Power-ups**: Temporary resource boosts

### Phase 3 Features
1. **NFT Integration**: Resources as tradeable NFTs
2. **Guilds**: Pool resources with teammates
3. **Leaderboards**: Top traders, efficient users
4. **Seasonal Events**: Special resource bonuses

## Implementation Checklist

- [ ] Set up project structure
- [ ] Implement base components
- [ ] Integrate Vaulta SDK
- [ ] Add animations
- [ ] Connect WebSocket feeds
- [ ] Implement state management
- [ ] Add error handling
- [ ] Write tests
- [ ] Performance optimization
- [ ] Accessibility audit
- [ ] Cross-platform testing
- [ ] Documentation
- [ ] Deploy beta version

## Key Success Metrics

1. **User Engagement**
    - Daily active users
    - Resource management actions/day
    - Time to first successful transaction

2. **Performance**
    - Transaction success rate >95%
    - App crash rate <0.1%
    - User retention after 7 days >60%

3. **Business**
    - RAM trading volume
    - Stake/unstake frequency
    - Support ticket reduction

## Notes for Developers

### Critical Implementation Points
1. **Never** show raw blockchain errors to users
2. **Always** preview transaction costs before execution
3. **Gracefully** handle network disconnections
4. **Optimize** for one-handed mobile use
5. **Test** with real blockchain data, not mocks

### Design Tokens
```css
/* Colors */
--primary-blue: #3b82f6;
--primary-green: #10b981;
--primary-gold: #fbbf24;
--epic-purple: #8b5cf6;
--bg-dark: #0a0a0f;
--bg-card: rgba(30, 30, 40, 0.8);

/* Spacing */
--spacing-xs: 4px;
--spacing-sm: 8px;
--spacing-md: 16px;
--spacing-lg: 24px;
--spacing-xl: 32px;

/* Animation */
--animation-fast: 200ms;
--animation-normal: 300ms;
--animation-slow: 500ms;
```

---

## Contact & Support

**Design Lead**: [Your Name]
**Technical Lead**: [Tech Lead Name]
**Blockchain Expert**: [Blockchain Dev Name]

For questions about implementation, refer to this document first, then reach out to the respective lead.

Last Updated: [Current Date]
Version: 1.0.0