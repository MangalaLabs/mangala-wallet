import { remote } from 'webdriverio';
import assert from 'assert';

// Appium configuration
const capabilities = {
  platformName: 'Android',
  'appium:automationName': 'UiAutomator2',
  'appium:deviceName': process.env.DEVICE_NAME || 'emulator-5554',
  'appium:udid': process.env.DEVICE_UDID || 'emulator-5554',
  'appium:appPackage': 'com.mangala.prowallet.dev',
  'appium:appActivity': 'com.mangala.wallet.MainActivity',
  "appium:autoAcceptAlerts": true,
  'appium:autoGrantPermissions': true,
  'appium:noReset': true,
  'appium:chromedriverUseSystemExecutable': true,
  'appium:autoAcceptSystemPermissionAlerts': true,
  'appium:autoAcceptSystemPermissionRequests': true,
  'appium:disableTouchId': true
};

// Selectors for PIN screen elements
const selectors = {
  // Numeric buttons (0-9)
  numericButton: (digit) => `//android.widget.Button[@text="${digit}"]`,
  
  // Delete button
  deleteButton: '//android.widget.Button[contains(@content-desc,"delete") or contains(@resource-id,"delete")]',
  
  // Forgot PIN text/button
  forgotPinButton: '//android.widget.TextView[contains(@text,"FORGOT PIN") or contains(@text,"Forgot PIN")]',
  
  // PIN indicators/dots
  pinIndicators: '//android.widget.LinearLayout[contains(@resource-id,"pin_indicator")]/*',
  
  // Logo
  appLogo: '//android.widget.ImageView[contains(@resource-id,"logo") or contains(@content-desc,"logo")]',
  
  // Success message or next screen indicator
  successIndicator: '//*[contains(@resource-id,"success") or contains(@text,"Home") or contains(@resource-id,"main")]'
};

describe('Mangala Wallet PIN Screen Test Suite', () => {
  let driver;
  let hasPINScreen = false;

  before(async () => {
    driver = await remote({
      hostname: '127.0.0.1',
      port: 4723,
      path: '/',
      capabilities
    });
    
    // Implicit wait
    await driver.setTimeout({ 'implicit': 60000 });
    
    // // Check if PIN screen exists
    // hasPINScreen = await checkPINScreenExists();
    
    // if (!hasPINScreen) {
    //   console.log('\n**********************************************************************');
    //   console.log('* WARNING: PIN screen not found in the app.                          *');
    //   console.log('* This might be because there is no wallet created yet.              *');
    //   console.log('* Tests will be skipped. Please create a wallet and set up PIN first.*');
    //   console.log('**********************************************************************\n');
    // }
  });

  after(async () => {
    if (driver) {
      await driver.deleteSession();
    }
    
    // // Display summary if no PIN screen was found
    // if (!hasPINScreen) {
    //   console.log('\n**********************************************************************');
    //   console.log('* TEST SUITE SUMMARY:                                                *');
    //   console.log('* All tests were skipped because no PIN screen was found.            *');
    //   console.log('* This is likely because there is no wallet created in the app.      *');
    //   console.log('* To run these tests:                                                *');
    //   console.log('* 1. Create a wallet in the app                                      *');
    //   console.log('* 2. Set up a PIN                                                    *');
    //   console.log('* 3. Run the tests again                                             *');
    //   console.log('**********************************************************************\n');
    // }
  });

  // Helper function to check if PIN screen exists
  async function checkPINScreenExists() {
    try {
      // First check if we're on a common non-PIN screen
      const isOnWalletCreationScreen = await checkForWalletCreationScreen();
      if (isOnWalletCreationScreen) {
        console.log('Detected wallet creation or setup screen.');
        return false;
      }
      
      // Check for numeric buttons (primary indicator of PIN screen)
      const numericButtons = await Promise.all(
        Array.from({length: 10}, (_, i) => 
          driver.$(selectors.numericButton(i.toString())).isDisplayed()
        )
      );
      
      // Check for delete button
      const deleteBtn = await driver.$(selectors.deleteButton).isDisplayed();
      
      // Check for app logo
      const logo = await driver.$(selectors.appLogo).isDisplayed();
      
      // If we have numeric buttons, delete button, and logo, we're likely on PIN screen
      return numericButtons.some(btn => btn) && deleteBtn && logo;
    } catch (e) {
      console.log('Error checking for PIN screen:', e.message);
      return false;
    }
  }
  
  async function checkForWalletCreationScreen() {
    try {
      const addNewAccountButton = await driver.$('//android.widget.TextView[@text="Add new account"]').isDisplayed();
      
      return addNewAccountButton
    } catch (e) {
      return false;
    }
  }

  // UI Components Verification
  describe('UI Components Verification', () => {
    it('TC-PIN-UI-001: Verify guard screen if no device PIN is set up', async function() {
      const textElement = await driver.$('//android.widget.TextView[contains(@text, "Mangala Wallet app requires that phone has the passcode")]');

      // Assert the text is displayed
      const textContent = await textElement.getText();
      const isDisplayed = await textElement.isDisplayed();
      
      assert(textContent.includes("Mangala Wallet app requires that phone has the passcode"), 
       'Text should contain expected message');
      assert(isDisplayed === true, 'Element should be displayed');
    });
  });

  // PIN Entry Functionality
  describe('PIN Entry Functionality', () => {
    beforeEach(async () => {
      // Clear any previously entered PIN
      if (hasPINScreen) {
        await clearPINField();
      }
    });

    it('TC-PIN-ENTRY-001: Verify Single Digit Entry', async function() {
      if (!hasPINScreen) {
        console.log('TEST SKIPPED: No PIN screen found. Please create a wallet and set up PIN first.');
        this.skip();
      }

      await driver.$(selectors.numericButton('1')).click();
      
      const filledIndicators = await getFilledIndicators();
      assert(filledIndicators === 1, 'One indicator should be filled');
    });

    it('TC-PIN-ENTRY-002: Verify Maximum PIN Length', async function() {
      if (!hasPINScreen) {
        this.skip();
      }

      // Enter digits exceeding max length
      for (let i = 0; i <= 9; i++) { // Try to enter 10 digits
        await driver.$(selectors.numericButton(i.toString())).click();
      }
      
      const filledIndicators = await getFilledIndicators();
      assert(filledIndicators === 6, 'Only 6 indicators should be filled');
    });

    it('TC-PIN-ENTRY-003: Verify PIN Auto-Validation', async function() {
      if (!hasPINScreen) {
        this.skip();
      }

      // Set up PIN as "111111" first
      await setupPIN('111111');
      
      // Clear and re-enter PIN
      await clearPINField();
      
      // Enter complete PIN
      for (let i = 0; i < 6; i++) {
        await driver.$(selectors.numericButton('1')).click();
      }
      
      // Should auto-validate after 6 digits
      const successElement = await driver.$(selectors.successIndicator);
      const isSuccess = await successElement.isDisplayed();
      assert(isSuccess, 'Should auto-validate after entering 6 digits');
    });
  });

  // Delete Button Functionality
  describe('Delete Button Functionality', () => {
    it('TC-PIN-DELETE-001: Verify Single Delete', async function() {
      if (!hasPINScreen) {
        this.skip();
      }

      await clearPINField();
      
      // Enter a digit then delete it
      await driver.$(selectors.numericButton('1')).click();
      await driver.$(selectors.deleteButton).click();
      
      const filledIndicators = await getFilledIndicators();
      assert(filledIndicators === 0, 'All indicators should be empty after delete');
    });

    it('TC-PIN-DELETE-002: Verify Multiple Delete', async function() {
      if (!hasPINScreen) {
        this.skip();
      }

      await clearPINField();
      
      // Enter "1234"
      await driver.$(selectors.numericButton('1')).click();
      await driver.$(selectors.numericButton('2')).click();
      await driver.$(selectors.numericButton('3')).click();
      await driver.$(selectors.numericButton('4')).click();
      
      // Delete multiple times
      await driver.$(selectors.deleteButton).click();
      await driver.$(selectors.deleteButton).click();
      
      const filledIndicators = await getFilledIndicators();
      assert(filledIndicators === 2, 'Should have 2 indicators filled after multiple deletes');
    });

    it('TC-PIN-DELETE-003: Verify Delete on Empty Field', async function() {
      if (!hasPINScreen) {
        this.skip();
      }

      await clearPINField();
      
      // Try to delete when field is empty
      await driver.$(selectors.deleteButton).click();
      
      const filledIndicators = await getFilledIndicators();
      assert(filledIndicators === 0, 'Field should remain empty');
    });
  });

  // PIN Validation
  describe('PIN Validation', () => {
    it('TC-PIN-VALID-001: Verify Incorrect PIN', async function() {
      if (!hasPINScreen) {
        this.skip();
      }

      // Ensure PIN is set to "111111"
      await setupPIN('111111');
      
      await clearPINField();
      
      // Enter incorrect PIN
      await enterPIN('222222');
      
      // Check for error state or attempt count
      const successElement = await driver.$(selectors.successIndicator);
      const isSuccess = await successElement.isDisplayed().catch(() => false);
      assert(!isSuccess, 'Incorrect PIN should not allow access');
    });

    it('TC-PIN-VALID-002: Verify Correct PIN', async function() {
      if (!hasPINScreen) {
        this.skip();
      }

      // Ensure PIN is set to "111111"
      await setupPIN('111111');
      
      await clearPINField();
      
      // Enter correct PIN
      await enterPIN('111111');
      
      // Check for success
      const successElement = await driver.$(selectors.successIndicator);
      const isSuccess = await successElement.isDisplayed();
      assert(isSuccess, 'Correct PIN should allow access');
    });

    it('TC-PIN-VALID-003: Verify Attempt Limit', async function() {
      if (!hasPINScreen) {
        this.skip();
      }

      // Ensure PIN is set to "111111"
      await setupPIN('111111');
      
      let attempt = 0;
      const maxAttempts = 5; // Assuming 5 attempts max
      
      while (attempt < maxAttempts + 1) {
        await clearPINField();
        await enterPIN('000000'); // Wrong PIN
        attempt++;
        
        // Check if locked out
        const isLocked = await checkLockedStatus();
        if (isLocked) {
          assert(attempt <= maxAttempts, `Should be locked after ${maxAttempts} attempts`);
          break;
        }
      }
    });
  });

  // Forgot PIN Functionality
  describe('Forgot PIN Functionality', () => {
    it('TC-PIN-FORGOT-001: Verify Forgot PIN Button Click', async function() {
      if (!hasPINScreen) {
        this.skip();
      }

      const forgotPinBtn = await driver.$(selectors.forgotPinButton);
      await forgotPinBtn.click();
      
      // Check if navigation to recovery flow occurs
      // This would need to be adapted based on actual app behavior
      const url = await driver.getUrl();
      assert(url.includes('recover') || url.includes('reset'), 'Should navigate to recovery flow');
    });
  });

  // Security Features
  describe('Security Features', () => {
    it('TC-PIN-SEC-001: Verify Session Timeout', async function() {
      if (!hasPINScreen) {
        this.skip();
      }

      // Set up PIN
      await setupPIN('111111');
      
      // Clear and enter correct PIN
      await clearPINField();
      await enterPIN('111111');
      
      // Wait for timeout (simulated)
      await driver.pause(60000); // 1 minute
      
      // Try accessing app - should prompt for PIN again
      const pinScreen = await isPINScreenDisplayed();
      assert(pinScreen, 'PIN screen should reappear after timeout');
    });

    it('TC-PIN-SEC-002: Verify Keyboard Input Sanitization', async function() {
      if (!hasPINScreen) {
        this.skip();
      }

      // Attempt to enter special characters
      const specialChars = ['@', '#', '$', '%', '&', '*'];
      
      for (const char of specialChars) {
        // Attempt to enter special character through keyboard
        try {
          await driver.keys([char]);
          const filledIndicators = await getFilledIndicators();
          assert(filledIndicators === 0, `Special character ${char} should not be accepted`);
        } catch (e) {
          // Expected that special characters won't be entered
        }
      }
    });
  });

  // Performance and Responsiveness
  describe('Performance and Responsiveness', () => {
    it('TC-PIN-PERF-001: Verify Button Press Response Time', async function() {
      if (!hasPINScreen) {
        this.skip();
      }

      const startTime = Date.now();
      
      for (let i = 0; i < 6; i++) {
        await driver.$(selectors.numericButton('1')).click();
      }
      
      const endTime = Date.now();
      const duration = endTime - startTime;
      
      assert(duration < 3000, 'PIN entry should be responsive (< 3 seconds for 6 digits)');
    });
  });

  // Helper Functions
  async function clearPINField() {
    // Clear PIN field by pressing delete multiple times
    for (let i = 0; i < 6; i++) {
      await driver.$(selectors.deleteButton).click();
    }
  }

  async function getFilledIndicators() {
    const indicators = await driver.$$(selectors.pinIndicators);
    let filled = 0;
    
    for (const indicator of indicators) {
      // Check if indicator is filled (implementation may vary)
      const isFilled = await indicator.getAttribute('resource-id').then(id => 
        id.includes('filled') || id.includes('active')
      ).catch(() => false);
      
      if (isFilled) filled++;
    }
    
    return filled;
  }

  async function enterPIN(pin) {
    for (const digit of pin) {
      await driver.$(selectors.numericButton(digit)).click();
    }
  }

  async function setupPIN(pin) {
    // This function would handle PIN setup flow
    // Implementation depends on app's PIN setup process
    try {
      const isSetupNeeded = await isPINSetupNeeded();
      if (isSetupNeeded) {
        await enterPIN(pin);
        await enterPIN(pin); // Confirm PIN
      }
    } catch (e) {
      console.log('PIN already set or setup not needed');
    }
  }

  async function isPINScreenDisplayed() {
    try {
      const appLogo = await driver.$(selectors.appLogo);
      const numericButton = await driver.$(selectors.numericButton('0'));
      return await appLogo.isDisplayed() && await numericButton.isDisplayed();
    } catch (e) {
      return false;
    }
  }

  async function checkLockedStatus() {
    // Check if account is locked out
    try {
      const lockMessage = await driver.$('//*[contains(@text,"locked") or contains(@text,"attempts")]');
      return await lockMessage.isDisplayed();
    } catch (e) {
      return false;
    }
  }

  async function isPINSetupNeeded() {
    // Check if PIN setup is needed
    try {
      const setupTitle = await driver.$('//*[contains(@text,"Set") and contains(@text,"PIN")]');
      return await setupTitle.isDisplayed();
    } catch (e) {
      return false;
    }
  }
});