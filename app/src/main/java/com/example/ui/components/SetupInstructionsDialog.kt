package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppLanguage
import com.example.ui.theme.*

@Composable
fun SetupInstructionsDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            color = SurfaceCard,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = EmeraldGreen)
                        Text(
                            text = if (language == AppLanguage.BN) "সেটআপ ও APK নির্দেশনা" else "Setup & APK Guide",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestDark
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card 1: Native Android & APK Export
                InstructionCard(
                    icon = Icons.Default.Smartphone,
                    titleBn = "১. সরাসরি APK ডাউনলোড ও অ্যান্ড্রয়েড ইনস্টলেশন",
                    titleEn = "1. Direct APK Download & Android Installation",
                    bodyBn = "এই অ্যাপটি অ্যান্ড্রয়েডের আধুনিক নেটিভ প্রযুক্তি (Kotlin + Jetpack Compose + Material Design 3) দিয়ে নিখুঁতভাবে তৈরি। AI Studio-র উপরের সেটিংস মেনু থেকে সরাসরি 'Download APK' বা 'Export ZIP' দিয়ে যেকোনো অ্যান্ড্রয়েড ফোনে বা Android Studio-তে চালাতে পারবেন।",
                    bodyEn = "This app is built natively in Android (Kotlin + Jetpack Compose + M3). You can download the installable APK directly from AI Studio settings or open the exported project in Android Studio."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Card 2: Future Capacitor Setup
                InstructionCard(
                    icon = Icons.Default.Smartphone,
                    titleBn = "২. ক্যাপাসিটর (Capacitor) হাইব্রিড কনফিগারেশন",
                    titleEn = "2. Future Capacitor Cross-Platform Packaging",
                    bodyBn = "ভবিষ্যতে যদি React/Web কোডবেসে রূপান্তর করে Capacitor দিয়ে প্যাকেজ করতে চান:\n1. `npm install @capacitor/core @capacitor/cli @capacitor/android`\n2. `npx cap init \"ZUKI Fit AI\" \"com.aistudio.zukifitai.app\"`\n3. `npx cap add android`\n4. `npx cap sync`\nএরপর অ্যান্ড্রয়েড বিল্ড জেনারেট করা যাবে।",
                    bodyEn = "To package as hybrid Capacitor:\n1. `npm install @capacitor/core @capacitor/cli @capacitor/android`\n2. `npx cap init \"ZUKI Fit AI\" \"com.aistudio.zukifitai.app\"`\n3. `npx cap add android`\n4. `npx cap sync`\nAnd build with Gradle or Android Studio."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Card 3: Secure API Key & Gemini
                InstructionCard(
                    icon = Icons.Default.Security,
                    titleBn = "৩. নিরাপদ API কি (API Key Security)",
                    titleEn = "3. Keeping API Keys Out of Frontend Code",
                    bodyBn = "Google Gemini AI কী কখনোই ফ্রন্টএন্ড কোডে বা ক্লায়েন্ট বান্ডেলে হার্ডকোড করা হয় না। এটি AI Studio Secrets প্যানেলে সংরক্ষিত থাকে এবং ব্যাকএন্ড / `BuildConfig.GEMINI_API_KEY` দ্বারা এনভায়রনমেন্ট ভ্যারিয়েবলের মাধ্যমে নিরাপদে অ্যাক্সেস পায়।",
                    bodyEn = "Gemini API keys are never hardcoded in client code. Keys reside securely in AI Studio Secrets panel and are injected via server environment or BuildConfig."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Card 4: Safe Sustainable Weight Loss Principle
                Surface(
                    color = MintGlow,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (language == AppLanguage.BN) "স্বাস্থ্যসম্মত ওজন নিয়ন্ত্রণ নীতিমালা" else "Safe Weight Management Policy",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == AppLanguage.BN)
                                "ZUKI Fit AI কখনোই না খেয়ে থাকা (starvation) বা চরম ক্র্যাশ ডায়েট সমর্থন করে না। দৈনিক ৩০০-৫০০ ক্যালরির মৃদু নিরাপদ ঘাটতি এবং নিয়মিত দেশি পুষ্টিকর খাবার গ্রহণই দীর্ঘস্থায়ী স্বাস্থ্য নিশ্চিত করে।"
                            else
                                "ZUKI Fit AI strictly advises against starvation or extreme crash diets. A safe deficit of 300-500 kcal/day and wholesome traditional foods ensures permanent, healthy results.",
                            fontSize = 12.sp,
                            color = ForestDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("close_instructions_button")
                ) {
                    Text(
                        text = if (language == AppLanguage.BN) "ঠিক আছে, বুঝেছি" else "Understood",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun InstructionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titleBn: String,
    titleEn: String,
    bodyBn: String,
    bodyEn: String
) {
    Surface(
        color = BackgroundLight,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(20.dp))
            Column {
                Text(text = titleBn, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForestDark)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = bodyBn, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)
            }
        }
    }
}
