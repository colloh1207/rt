package com.sdd.marketplace.feature.static.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } },
                title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp)) {
            Text("Effective Date: January 1, 2025", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text("At Sdd Marketplace, your privacy is our priority. This policy explains what data we collect, why we collect it, and how you can control it.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            TermsSection("1. Information We Collect") { """We collect information you provide directly:
• Account information: Name, email address, phone number, profile photo
• Identity verification (KYC): Government ID, selfie photos (stored encrypted)
• Payment information: Payment methods (stored via our payment processors, not on our servers)
• Communications: Messages, reviews, and support tickets you send
• Location: City/state if you choose to add it to your profile

We automatically collect:
• Device information: Device ID, OS version, app version
• Usage data: Pages viewed, features used, time spent
• Technical data: IP address, crash reports, performance data""" }
            TermsSection("2. How We Use Your Information") { """We use your information to:
• Provide, maintain, and improve the App
• Process transactions and send related notifications
• Verify your identity for KYC compliance
• Detect and prevent fraud and abuse
• Send you important service updates and security alerts
• Provide customer support
• Comply with legal obligations

We do NOT:
• Sell your personal data to third parties
• Use your data for targeted advertising without your consent
• Share your financial information with other users""" }
            TermsSection("3. Information Sharing") { """We share your information only in these circumstances:
• With sellers/buyers as necessary to complete transactions (name, shipping address)
• With payment processors (Razorpay, PayPal) for transaction processing
• With service providers who help us operate the App (under strict confidentiality agreements)
• When required by law, court order, or to protect our legal rights
• In connection with a business merger or acquisition (with notice to you)

Your public profile information (name, profile photo, ratings) is visible to other users.""" }
            TermsSection("4. Data Security") { """We implement industry-standard security measures:
• All data transmitted is encrypted using TLS 1.3
• Passwords are hashed using bcrypt
• KYC documents are stored encrypted and access is logged
• Payment data is processed by PCI-DSS compliant processors
• We conduct regular security audits
• Device-level database encryption using SQLCipher

Despite these measures, no system is 100% secure. We cannot guarantee absolute security.""" }
            TermsSection("5. Data Retention") { """We retain your data as follows:
• Account data: Until you delete your account + 30 days
• Transaction records: 7 years (legal requirement)
• Chat messages: 1 year after the conversation ends
• KYC documents: 5 years from verification
• Support tickets: 2 years from closure
• App usage logs: 90 days

After deletion, your data is removed from our active systems within 30 days. Some data may remain in backups for up to 90 days.""" }
            TermsSection("6. Your Rights") { """You have the right to:
• Access a copy of your personal data (submit request via Help & Support)
• Correct inaccurate personal data
• Delete your account and associated data
• Restrict processing of your data
• Data portability - receive your data in a machine-readable format
• Opt out of marketing communications
• Lodge a complaint with your local data protection authority

To exercise any of these rights, go to Settings > Help & Support > Submit Request, or email privacy@sddmarketplace.com. We respond to all requests within 30 days.""" }
            TermsSection("7. Cookies & Tracking") { """We use minimal tracking technologies:
• Session cookies: Required for app functionality
• Analytics: Aggregated, anonymized usage statistics only
• Crash reporting: To identify and fix bugs
• No advertising trackers or cross-site tracking

You cannot opt out of functional cookies without disabling the app's functionality.""" }
            TermsSection("8. Children's Privacy") { "Sdd Marketplace is not intended for users under 18. We do not knowingly collect data from minors. If we discover we have collected data from a minor without parental consent, we will delete it immediately. Parents who believe their child has created an account should contact us at privacy@sddmarketplace.com." }
            TermsSection("9. Changes to This Policy") { "We may update this Privacy Policy periodically. We will notify you of significant changes via the App or email. Continued use of the App after changes constitutes your acceptance of the new policy." }
            TermsSection("10. Contact Us") { "Privacy Officer: privacy@sddmarketplace.com\nData Protection: dpo@sddmarketplace.com\nAddress: Sdd Marketplace Pvt. Ltd., New Delhi, India 110001" }
        }
    }
}
