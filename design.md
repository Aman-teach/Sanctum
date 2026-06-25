---
name: Sanctum Design System
colors:
  surface: '#fbf9f8'
  surface-dim: '#dbdad9'
  surface-bright: '#fbf9f8'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f5f3f2'
  surface-container: '#efedec'
  surface-container-high: '#e9e8e7'
  surface-container-highest: '#e4e2e1'
  on-surface: '#1b1c1b'
  on-surface-variant: '#40484e'
  inverse-surface: '#303030'
  inverse-on-surface: '#f2f0ef'
  outline: '#70787e'
  outline-variant: '#bfc8cf'
  surface-tint: '#00668a'
  primary: '#004d6a'
  on-primary: '#ffffff'
  primary-container: '#00668b'
  on-primary-container: '#aee0ff'
  inverse-primary: '#87cff9'
  secondary: '#51606b'
  on-secondary: '#ffffff'
  secondary-container: '#d2e2ef'
  on-secondary-container: '#556570'
  tertiary: '#4f4161'
  on-tertiary: '#ffffff'
  tertiary-container: '#685979'
  on-tertiary-container: '#e5d2f9'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#c4e7ff'
  primary-fixed-dim: '#87cff9'
  on-primary-fixed: '#001e2c'
  on-primary-fixed-variant: '#004c69'
  secondary-fixed: '#d5e5f2'
  secondary-fixed-dim: '#b9c9d5'
  on-secondary-fixed: '#0e1d27'
  on-secondary-fixed-variant: '#3a4953'
  tertiary-fixed: '#eedcff'
  tertiary-fixed-dim: '#d2bfe5'
  on-tertiary-fixed: '#221632'
  on-tertiary-fixed-variant: '#4f4160'
  background: '#fbf9f8'
  on-background: '#1b1c1b'
  surface-variant: '#e4e2e1'
typography:
  headline-lg:
    fontFamily: Manrope
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Manrope
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Manrope
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  title-lg:
    fontFamily: Manrope
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Manrope
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Manrope
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Manrope
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.5px
  label-md:
    fontFamily: Manrope
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 16px
  headline-lg-mobile:
    fontFamily: Manrope
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  edge-margin: 16px
  gutter: 12px
  touch-target: 48px
---

## Brand & Style
The design system is anchored in the "High-Performance Utility" philosophy, specifically optimized for the mobile browsing experience. It prioritizes clarity, speed, and one-handed ergonomics. The brand personality is professional, calm, and highly organized, aiming to provide a focused environment for information consumption.

The design style is a refined implementation of **Modern Corporate Minimalism**. It leverages Material 3 logic—using tonal surface shifts to define hierarchy—while stripping away unnecessary decoration. The aesthetic is clean and surgical, ensuring that the user's content remains the primary focus while the browser interface recedes into a supportive, trusted frame.

## Colors
The palette is dominated by a clean, off-white foundation to reduce eye strain during long reading sessions. 

- **Primary**: A deep, calm blue-teal used for active states, primary actions, and progress indicators.
- **Surface & Containers**: The "Sanctum" feel is achieved by using #FBF9F8 as the base canvas, with pure white (#FFFFFF) reserved for raised elements like the URL bar and active cards. Soft gray (#F3F4F6) is used for recessed areas like the tab switcher background or inactive menu items.
- **Grayscale**: High-contrast grays ensure maximum legibility. Text uses a near-black for primary headers and a muted slate for secondary metadata.
- **Status**: Functional colors are desaturated to maintain the professional tone while remaining clear in meaning.

## Typography
Manrope was selected for its modern, geometric structure and exceptional legibility at small scales. 

- **Hierarchy**: Headlines use tighter letter-spacing and heavier weights to feel "anchored." 
- **Body Text**: Line heights are generous (1.5x) to facilitate comfortable reading of long-form web content.
- **Labels**: Small labels use increased letter-spacing and semi-bold weights to remain functional and scannable even at 11px or 12px.
- **Scale**: The system uses a major-second scale for harmony, with specific mobile overrides for large display text to prevent excessive wrapping.

## Layout & Spacing
This design system utilizes an **8px grid system** for all spatial relationships. 

- **One-Handed Use**: Interactive elements (URL bar, navigation, tab controls) are prioritized for the bottom "thumb zone" of the mobile device. 
- **Margins**: A standard 16px horizontal margin is applied to all mobile screens to ensure content doesn't bleed into the physical device edges.
- **Touch Targets**: All interactive elements (buttons, icons, menu items) must maintain a minimum hit area of 48x48px, even if the visual representation is smaller.
- **Grids**: For internal components like the "Top Sites" or "Tab Grid," a 2-column or 4-column fluid layout is used with a 12px gutter.

## Elevation & Depth
In alignment with Material 3, elevation is primarily communicated through **Tonal Layers** rather than shadows.

- **Level 0 (Base)**: #FBF9F8 (Off-white). Used for the main background.
- **Level 1 (Default Surface)**: #FFFFFF (White). Used for the URL bar, main content area, and primary navigation bars.
- **Level 2 (Containers)**: #F3F4F6 (Soft Gray). Used for background cards in the tab switcher or search suggestions.
- **Level 3 (Pop-ups/Sheets)**: #FFFFFF with a very subtle 4% opacity primary-color tint.

Shadows are used sparingly, reserved only for floating elements like the Bottom Sheet or Context Menus. These shadows are "Ambient": very long, diffused, and low-opacity (4-8% Alpha) to avoid a cluttered look.

## Shapes
The shape language follows a "Variable Softness" approach to differentiate element types:

- **Extra Large (24px - 28px)**: Used for Bottom Sheets, the URL bar (pill shape), and large container overlays. This creates a friendly, approachable container for content.
- **Medium (8px - 12px)**: Used for Action Buttons and Tab Previews. This provides enough structure to feel professional and "high-performance."
- **Small (4px)**: Used for specific tooltips or checkboxes.
- **Pill**: Primary "Call to Action" buttons and the "Active Tab" indicator use fully rounded ends for maximum visual distinction.

## Components

- **URL Bar (The Omnibox)**: Positioned at the bottom for accessibility. It is a pill-shaped container (#FFFFFF) with a subtle 1px border (#E5E7EB).
- **Buttons**:
    - *Primary*: Filled with #00668B, white text, 12px roundedness.
    - *Secondary*: Outlined (#E5E7EB) or Ghost.
- **Tabs**: Tab previews use a 12px rounded corner. The active tab is outlined with a 2px #00668B border.
- **Bottom Sheets**: Use a 28px top-corner radius. They must include a "handle" indicator (32x4px, #E5E7EB) at the top center.
- **Input Fields**: Soft gray backgrounds (#F3F4F6) with no borders in the default state, shifting to a 2px #00668B bottom-border or outline upon focus.
- **Chips**: Used for search filters or history categories. Small, 8px rounded rectangles with #F3F4F6 backgrounds and #44474E text.
- **Lists**: Clean, divider-less lists using 16px vertical padding. Subtle #F3F4F6 press-states for haptic feedback.
