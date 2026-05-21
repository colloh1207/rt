package com.sdd.marketplace.feature.static.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sdd.marketplace.core.navigation.Screen
import com.sdd.marketplace.core.ui.theme.SddPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsConditionsScreen(navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("General", "Sellers", "Buyers")

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } },
                title = { Text("Terms & Conditions", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab, contentColor = SddPink) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                }
            }
            when (selectedTab) {
                0 -> GeneralTermsContent()
                1 -> SellerTermsContent()
                2 -> BuyerTermsContent()
            }
        }
    }
}

@Composable
fun GeneralTermsContent() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Last updated: January 1, 2025", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        TermsSection("1. Acceptance of Terms") {
            """By accessing or using the Sdd Marketplace application ("App"), you agree to be bound by these Terms and Conditions ("Terms"). If you do not agree to these Terms, please do not use the App. These Terms apply to all users, including buyers, sellers, and visitors.

We reserve the right to modify these Terms at any time. Continued use of the App after modifications constitutes your acceptance of the new Terms. You will be notified of significant changes via the App or by email."""
        }
        TermsSection("2. Account Registration") {
            """You must create an account to use most features of the App. You agree to:
• Provide accurate, complete, and current information
• Keep your login credentials secure and confidential  
• Notify us immediately of any unauthorized access to your account
• Be responsible for all activities that occur under your account
• Not create accounts using false identities or for fraudulent purposes
• A maximum of 2 (two) accounts per device is permitted

Users under the age of 18 may not create an account or use the App without verifiable parental consent."""
        }
        TermsSection("3. Prohibited Conduct") {
            """You agree not to:
• Post false, misleading, or fraudulent content
• Harass, threaten, or abuse other users
• Use the App for illegal activities
• Circumvent any security features of the App
• Scrape, reverse engineer, or copy the App's code or content
• Create fake reviews or manipulate ratings
• Use automated systems to access the App without permission
• Spam or send unsolicited communications
• Engage in market manipulation or price fixing
• Sell counterfeit, stolen, or prohibited items"""
        }
        TermsSection("4. Intellectual Property") {
            """All content on the App, including logos, text, graphics, and software, is the property of Sdd Marketplace or its content suppliers. You may not reproduce, distribute, or create derivative works without our written consent.

By posting content on the App, you grant us a non-exclusive, royalty-free, worldwide license to use, display, and distribute that content in connection with our services."""
        }
        TermsSection("5. Dispute Resolution") {
            """Any disputes between users must first be attempted to be resolved directly between the parties. If resolution cannot be reached, users may escalate to our dispute resolution team through the Help & Support section.

We reserve the right to mediate disputes but are not obligated to do so. Our decisions in moderation disputes are final."""
        }
        TermsSection("6. Limitation of Liability") {
            """To the maximum extent permitted by applicable law, Sdd Marketplace shall not be liable for any indirect, incidental, special, consequential, or punitive damages arising from your use of the App.

Our total liability to you for any claims arising from your use of the App shall not exceed the amount you paid to us in the 12 months preceding the claim."""
        }
        TermsSection("7. Governing Law") {
            "These Terms are governed by the laws of India. Any legal action arising from these Terms shall be subject to the exclusive jurisdiction of courts in New Delhi, India."
        }
        TermsSection("8. Contact") {
            "For questions about these Terms, contact us at legal@sddmarketplace.com or through the Help & Support section of the App."
        }
    }
}

@Composable
fun SellerTermsContent() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Last updated: January 1, 2025", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SddPink.copy(alpha = 0.08f)), shape = RoundedCornerShape(12.dp)) {
            Row(Modifier.padding(16.dp)) {
                Icon(Icons.Filled.Store, "Seller", tint = SddPink)
                Spacer(Modifier.width(12.dp))
                Text("These additional terms apply to all sellers on Sdd Marketplace. General Terms also apply.", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(16.dp))
        TermsSection("1. Seller Eligibility") {
            """To sell on Sdd Marketplace you must:
• Be at least 18 years of age
• Provide accurate personal and business information
• Comply with all applicable laws in your jurisdiction
• Complete KYC (Know Your Customer) verification for certain transaction limits
• Maintain a valid payment method for receiving payouts
• Not have been previously banned from the platform"""
        }
        TermsSection("2. Listing Requirements") {
            """All product listings must:
• Be accurate, truthful, and not misleading
• Include clear, representative photographs of the actual item
• State the correct condition (New, Like New, Good, Fair, Poor)
• Comply with applicable product safety and labeling regulations
• Not include prohibited or restricted items (see Prohibited Items Policy)
• Be priced in good faith at market value

New sellers are limited to 2 listings per day for the first 30 days. This limit may be increased based on account standing."""
        }
        TermsSection("3. Transaction Obligations") {
            """Upon accepting an order, sellers must:
• Confirm the order within 24 hours
• Ship items within the stated handling time (maximum 5 business days)
• Provide valid tracking information
• Package items securely to prevent damage in transit
• Honor all stated return and refund policies
• Communicate promptly with buyers about any issues

Failure to fulfill confirmed orders may result in seller account suspension."""
        }
        TermsSection("4. Seller Fees & Payouts") {
            """Sdd Marketplace charges:
• A 5% transaction fee on all completed sales
• A 2% payment processing fee
• No listing fees for standard listings

Payouts are processed within 3-5 business days after order confirmation. Sellers must maintain a valid payout method. Sdd Marketplace reserves the right to withhold payouts during dispute investigations."""
        }
        TermsSection("5. Returns & Refunds") {
            """Sellers must honor the return policy stated in their listings. Minimum return window is 7 days for items not as described. Sellers are responsible for return shipping costs if the item was not as described. Repeated refund requests may indicate listing quality issues and may affect seller standing."""
        }
        TermsSection("6. Prohibited Items") {
            """The following items are strictly prohibited:
• Counterfeit, replica, or unauthorized goods
• Weapons, firearms, or dangerous materials
• Illegal drugs or controlled substances
• Items that infringe on intellectual property
• Adult content or services
• Live animals
• Human remains or body parts
• Items subject to recall or safety bans

Violations may result in immediate account termination and legal action."""
        }
        TermsSection("7. Seller Ratings & Reviews") {
            """Sellers are rated by verified buyers. You must not:
• Incentivize positive reviews
• Threaten or harass buyers who leave negative reviews
• Manipulate review scores through fake purchases

Sellers with consistent ratings below 2.0 stars may have listings removed."""
        }
        TermsSection("8. Account Suspension") {
            """Seller accounts may be suspended or permanently banned for:
• Multiple confirmed fraud reports
• Consistent failure to fulfill orders
• Selling prohibited items
• Rating manipulation
• Terms violations

Suspended sellers may appeal through our dispute resolution process."""
        }
    }
}

@Composable
fun BuyerTermsContent() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Last updated: January 1, 2025", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SddPink.copy(alpha = 0.08f)), shape = RoundedCornerShape(12.dp)) {
            Row(Modifier.padding(16.dp)) {
                Icon(Icons.Filled.ShoppingCart, "Buyer", tint = SddPink)
                Spacer(Modifier.width(12.dp))
                Text("These additional terms apply to all buyers on Sdd Marketplace. General Terms also apply.", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(16.dp))
        TermsSection("1. Purchase Agreement") {
            """When you place an order on Sdd Marketplace:
• You are entering into a binding purchase agreement with the seller
• You agree to pay the full listed price plus applicable fees
• You confirm all order details (size, color, quantity) are correct
• You acknowledge our Buyer Protection policy terms

Orders cannot be cancelled after they have been confirmed by the seller, except where permitted by our cancellation policy."""
        }
        TermsSection("2. Payment") {
            """You must provide a valid payment method. By submitting an order:
• You authorize Sdd Marketplace to charge your payment method
• You agree not to dispute valid charges with your bank
• You understand that payment is processed securely through our payment partners
• You may save payment methods for future use

Fraudulent chargebacks may result in account suspension and legal action."""
        }
        TermsSection("3. Buyer Protection") {
            """Our Buyer Protection covers you when:
• An item is significantly not as described
• An item does not arrive within the stated delivery window
• A seller does not ship after payment
• An item arrives damaged due to inadequate packaging

Buyer Protection does not cover buyer's remorse, change of mind, or issues with correctly described items."""
        }
        TermsSection("4. Reviews & Feedback") {
            """You may leave reviews for sellers after completing a purchase. Reviews must:
• Be honest and based on your genuine experience
• Not contain offensive language or personal attacks
• Not be submitted as part of any incentive scheme

Review eligibility: Accounts must be at least 3 weeks old to post reviews. This helps maintain review authenticity and prevents fake feedback."""
        }
        TermsSection("5. Prohibited Buyer Conduct") {
            """Buyers must not:
• Purchase items with intent to defraud sellers
• File false "item not received" or "not as described" claims
• Use stolen or fraudulent payment methods
• Abuse return policies
• Harass sellers for faster delivery
• Leave retaliatory or false negative reviews"""
        }
        TermsSection("6. Dispute Resolution") {
            """If you have a problem with your order:
1. First, contact the seller directly through the in-app chat
2. If unresolved after 48 hours, open a dispute through Order Detail > Request Refund
3. Our team will review evidence from both parties within 5 business days
4. Our decision is final but may be appealed within 14 days"""
        }
    }
}

@Composable
fun TermsSection(title: String, content: () -> String) {
    var expanded by remember { mutableStateOf(true) }
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SddPink, modifier = Modifier.weight(1f))
            Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, "Toggle", tint = SddPink)
        }
        if (expanded) {
            Text(content(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
            Spacer(Modifier.height(8.dp))
        }
        Divider()
    }
}
