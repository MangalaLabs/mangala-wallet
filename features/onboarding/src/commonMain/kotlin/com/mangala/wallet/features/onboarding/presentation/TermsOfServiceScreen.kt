package com.mangala.wallet.features.onboarding.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class TermsOfServiceScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        OnboardingGradientBackground(
            afterBackgroundModifier = Modifier.safeDrawingPadding().imePadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = MR.strings.title_terms_of_service.desc().localized(),
                    onBackClicked = navigator::pop
                )
                
                // Scrollable content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp)
                ) {
                    // Effective Date
                    Text(
                        text = "Effective Date: 01 July 2025",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = getInterFontFamily(),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Introduction
                    Text(
                        text = "Welcome to Mangala, a crypto wallet application developed by Mangala Labs, a small research group based in Hanoi, Vietnam.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFD1D1D1),
                        lineHeight = 22.4.sp,
                        fontFamily = getInterFontFamily(),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "Please read these Terms of Service (\"Terms\") carefully before using the Mangala application (\"App\", \"Service\"). By using the App, you agree to be bound by these Terms.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFD1D1D1),
                        lineHeight = 22.4.sp,
                        fontFamily = getInterFontFamily(),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    // Section 1: Overview
                    SectionHeader("1. Overview")
                    SectionText("Mangala is a non-custodial crypto wallet supporting multiple blockchains, including Bitcoin, EVM-compatible networks, and Vaulta (formerly EOS). Users may create or import wallets. Mangala does not store your private keys or seed phrases on its servers. You retain full control and responsibility over your cryptographic assets.")
                    
                    // Section 2: Eligibility  
                    SectionHeader("2. Eligibility")
                    SectionText("Mangala is available to users globally without age restriction. However, by using the app, you confirm that you are legally capable of managing your own financial assets in your jurisdiction.")
                    
                    // Section 3: Key Features
                    SectionHeader("3. Key Features")
                    SectionText("Mangala enables users to:")
                    BulletPoint("Create/import non-custodial wallets")
                    BulletPoint("Send/receive tokens across supported blockchains")
                    BulletPoint("Buy, sell, and swap crypto assets")
                    BulletPoint("Stake tokens on supported chains")
                    BulletPoint("Use a built-in AI assistant (requires passkey login)")
                    BulletPoint("Recover wallets securely from private keys or seed phrases")
                    SectionText("The App integrates select third-party services such as Infura, Chainlink, and CoinGecko for real-time data and blockchain interaction.")
                    
                    // Section 4: Security & Responsibility
                    SectionHeader("4. Security & Responsibility")
                    BulletPoint("Private keys and seed phrases are stored locally on your device, encrypted with advanced security protocols.")
                    BulletPoint("Mangala Labs does not have access to, and cannot recover, your private keys or wallet.")
                    BulletPoint("You are solely responsible for maintaining access to your wallet. We cannot assist in recovery if you lose your credentials.")
                    
                    // Section 5: Acceptable Use Policy
                    SectionHeader("5. Acceptable Use Policy")
                    SectionText("You agree not to use Mangala for any of the following:")
                    BulletPoint("Money laundering or terrorism financing")
                    BulletPoint("Fraud, phishing, or social engineering")
                    BulletPoint("Sending spam or unauthorized access attempts")
                    BulletPoint("Any activity that violates applicable laws or third-party rights")
                    SectionText("Mangala Labs reserves the right to restrict access or report suspicious activity to authorities.")
                    
                    // Section 6: Premium Features & Refunds
                    SectionHeader("6. Premium Features & Refunds")
                    SectionText("Mangala offers an optional Premium Plan with enhanced features.")
                    SectionText("Refunds may be granted under specific circumstances, subject to internal review and at the discretion of Mangala Labs.")
                    
                    // Section 7: Risks & Disclaimer
                    SectionHeader("7. Risks & Disclaimer")
                    BulletPoint("Cryptocurrencies are volatile and inherently risky.")
                    BulletPoint("You understand and accept that Mangala Labs is not liable for financial loss, stolen funds, or user mistakes.")
                    BulletPoint("Use of the App is at your own risk. Always backup your seed phrase in a secure offline location.")
                    
                    // Section 8: Limitation of Liability
                    SectionHeader("8. Limitation of Liability")
                    SectionText("Mangala Labs provides the App \"as-is\" with no warranties, expressed or implied.")
                    SectionText("We do not guarantee uninterrupted access or error-free operation.")
                    
                    // Section 9: Governing Law & Dispute Resolution
                    SectionHeader("9. Governing Law & Dispute Resolution")
                    SectionText("As of now, Mangala Labs is not registered under any national jurisdiction and has no formal dispute resolution mechanism.")
                    SectionText("We are a research initiative and do not operate as a regulated financial service.")
                    
                    // Section 10: Changes to Terms
                    SectionHeader("10. Changes to Terms")
                    SectionText("We may update these Terms from time to time. Continued use of the App after changes implies acceptance of the new Terms.")
                    
                    // Section 11: Contact Us
                    SectionHeader("11. Contact Us")
                    SectionText("For questions or support, please contact:")
                    Text(
                        text = "📧 mangalacryptowallet@gmail.com",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3B90FF),
                        fontFamily = getInterFontFamily(),
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        fontFamily = getInterFontFamily(),
        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
    )
}

@Composable
private fun SectionText(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        color = Color(0xFFD1D1D1),
        lineHeight = 22.4.sp,
        fontFamily = getInterFontFamily(),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "• ",
            fontSize = 16.sp,
            color = Color(0xFFD1D1D1),
            fontFamily = getInterFontFamily(),
            modifier = Modifier.padding(end = 8.dp, top = 2.dp)
        )
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFD1D1D1),
            lineHeight = 22.4.sp,
            fontFamily = getInterFontFamily(),
            modifier = Modifier.weight(1f)
        )
    }
}