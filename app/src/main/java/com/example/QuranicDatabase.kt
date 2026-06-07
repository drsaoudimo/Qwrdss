package com.example

import android.content.Context
import org.json.JSONObject
import java.io.InputStream
import kotlin.math.sin

data class Surah(
    val index: Int, // 1 to 114
    val nameArabic: String,
    val nameEnglish: String,
    val englishMeaning: String,
    val totalVerses: Int,
    val revelationOrder: Int,
    val type: String // "Meccan" or "Medinan"
)

object QuranicDatabase {

    val surahs: List<Surah> = listOf(
        Surah(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, 5, "Meccan"),
        Surah(2, "البقرة", "Al-Baqarah", "The Cow", 286, 87, "Medinan"),
        Surah(3, "آل عمران", "Al-Imran", "Family of Imran", 200, 89, "Medinan"),
        Surah(4, "النساء", "An-Nisa", "The Women", 176, 92, "Medinan"),
        Surah(5, "المائدة", "Al-Ma'idah", "The Table", 120, 112, "Medinan"),
        Surah(6, "الأنعام", "Al-An'am", "The Cattle", 165, 55, "Meccan"),
        Surah(7, "الأعراف", "Al-A'raf", "The Heights", 206, 39, "Meccan"),
        Surah(8, "الأنفال", "Al-Anfal", "The Spoils of War", 75, 88, "Medinan"),
        Surah(9, "التوبة", "At-Tawbah", "The Repentance", 129, 113, "Medinan"),
        Surah(10, "يونس", "Yunus", "Jonah", 109, 51, "Meccan"),
        Surah(11, "هود", "Hud", "Hud", 123, 52, "Meccan"),
        Surah(12, "يوسف", "Yunus", "Joseph", 111, 53, "Meccan"),
        Surah(13, "الرعد", "Ar-Ra'd", "The Thunder", 43, 96, "Medinan"),
        Surah(14, "إبراهيم", "Ibrahim", "Abraham", 52, 72, "Meccan"),
        Surah(15, "الحجر", "Al-Hijr", "The Rocky Tract", 99, 54, "Meccan"),
        Surah(16, "النحل", "An-Nahl", "The Bee", 128, 70, "Meccan"),
        Surah(17, "الإسراء", "Al-Isra", "The Night Journey", 111, 50, "Meccan"),
        Surah(18, "الكهف", "Al-Kahf", "The Cave", 110, 69, "Meccan"),
        Surah(19, "مريم", "Maryam", "Mary", 98, 44, "Meccan"),
        Surah(20, "طه", "Taha", "Ta-Ha", 135, 45, "Meccan"),
        Surah(21, "الأنبياء", "Al-Anbiya", "The Prophets", 112, 73, "Meccan"),
        Surah(22, "الحج", "Al-Hajj", "The Pilgrimage", 78, 103, "Medinan"),
        Surah(23, "المؤمنون", "Al-Mu'minun", "The Believers", 118, 74, "Meccan"),
        Surah(24, "النور", "An-Nur", "The Light", 64, 102, "Medinan"),
        Surah(25, "الفرقان", "Al-Furqan", "The Criterion", 77, 42, "Meccan"),
        Surah(26, "الشعراء", "As-Shu'ara", "The Poets", 227, 47, "Meccan"),
        Surah(27, "النمل", "An-Naml", "The Ant", 93, 48, "Meccan"),
        Surah(28, "القصص", "Al-Qasas", "The Stories", 88, 49, "Meccan"),
        Surah(29, "العنكبوت", "Al-Ankabut", "The Spider", 69, 85, "Medinan"),
        Surah(30, "الروم", "Ar-Rum", "The Romans", 60, 84, "Meccan"),
        Surah(31, "لقمان", "Luqman", "Luqman", 34, 57, "Meccan"),
        Surah(32, "السجدة", "As-Sajdah", "The Prostration", 30, 75, "Meccan"),
        Surah(33, "الأحزاب", "Al-Ahzab", "The Combined Forces", 73, 90, "Medinan"),
        Surah(34, "سبأ", "Saba", "Sheba", 54, 58, "Meccan"),
        Surah(35, "فاطر", "Fatir", "The Originator", 45, 43, "Meccan"),
        Surah(36, "يس", "Yasin", "Ya-Sin", 83, 41, "Meccan"),
        Surah(37, "الصافات", "As-Saffat", "Those Ranges in Ranks", 182, 56, "Meccan"),
        Surah(38, "ص", "Sad", "The Letter Sad", 88, 38, "Meccan"),
        Surah(39, "الزمر", "Az-Zumar", "The Groups", 75, 59, "Meccan"),
        Surah(40, "غافر", "Ghafir", "The Forgiver", 85, 60, "Meccan"),
        Surah(41, "فصلت", "Fussilat", "Explained in Detail", 54, 61, "Meccan"),
        Surah(42, "الشورى", "As-Shura", "The Consultation", 53, 62, "Meccan"),
        Surah(43, "الزخرف", "Az-Zukhruf", "The Gold Adornments", 89, 63, "Meccan"),
        Surah(44, "الدخان", "Ad-Dukhan", "The Smoke", 59, 64, "Meccan"),
        Surah(45, "الجاثية", "Al-Jathiyah", "The Crouching", 37, 65, "Meccan"),
        Surah(46, "الأحقاف", "Al-Ahqaf", "The Wind-Curved Sandhills", 35, 66, "Meccan"),
        Surah(47, "محمد", "Muhammad", "Muhammad", 38, 95, "Medinan"),
        Surah(48, "الفتح", "Al-Fath", "The Victory", 29, 111, "Medinan"),
        Surah(49, "الحجرات", "Al-Hujurat", "The Dwellings", 18, 106, "Medinan"),
        Surah(50, "ق", "Qaf", "The Letter Qaf", 45, 34, "Meccan"),
        Surah(51, "الذاريات", "Ad-Dhariyat", "The Winnowing Winds", 60, 67, "Meccan"),
        Surah(52, "الطور", "At-Tur", "The Mount", 49, 76, "Meccan"),
        Surah(53, "النجم", "An-Najm", "The Star", 62, 23, "Meccan"),
        Surah(54, "المرسلون", "Al-Qamar", "The Moon", 55, 37, "Meccan"),
        Surah(55, "الرحمن", "Ar-Rahman", "The Beneficent", 78, 97, "Medinan"),
        Surah(56, "الواقعة", "Al-Waqi'ah", "The Inevitable", 96, 46, "Meccan"),
        Surah(57, "الحديد", "Al-Hadid", "The Iron", 29, 94, "Medinan"),
        Surah(58, "المجادلة", "Al-Mujadilah", "The Pleading Woman", 22, 105, "Medinan"),
        Surah(59, "الحشر", "Al-Hashr", "The Exile", 24, 101, "Medinan"),
        Surah(60, "الممتحنة", "Al-Mumtahanah", "She That is to be Examined", 13, 91, "Medinan"),
        Surah(61, "الصف", "As-Saff", "The Ranks", 14, 109, "Medinan"),
        Surah(62, "الجمعة", "Al-Jumu'ah", "The Congregation", 11, 110, "Medinan"),
        Surah(63, "المنافقون", "Al-Munafiqun", "The Hypocrites", 11, 104, "Medinan"),
        Surah(64, "التغابن", "At-Taghabun", "The Mutual Disillusion", 18, 108, "Medinan"),
        Surah(65, "الطلاق", "At-Talaq", "The Divorce", 12, 99, "Medinan"),
        Surah(66, "التحريم", "At-Tahrim", "The Prohibition", 12, 107, "Medinan"),
        Surah(67, "الملك", "Al-Mulk", "The Sovereignty", 30, 77, "Meccan"),
        Surah(68, "القلم", "Al-Qalam", "The Pen", 52, 2, "Meccan"),
        Surah(69, "الحاقة", "Al-Haqqah", "The Reality", 52, 78, "Meccan"),
        Surah(70, "المعارج", "Al-Ma'arij", "The Ascending Stairways", 44, 79, "Meccan"),
        Surah(71, "نوح", "Nuh", "Noah", 28, 71, "Meccan"),
        Surah(72, "الجن", "Al-Jinn", "The Jinn", 28, 40, "Meccan"),
        Surah(73, "المزمل", "Al-Muzzammil", "The Enshrouded One", 20, 3, "Meccan"),
        Surah(74, "المدثر", "Al-Muddaththir", "The Cloaked One", 56, 4, "Meccan"),
        Surah(75, "القيامة", "Al-Qiyamah", "The Resurrection", 40, 31, "Meccan"),
        Surah(76, "الإنسان", "Al-Insan", "The Man", 31, 98, "Medinan"),
        Surah(77, "المرسلات", "Al-Mursalat", "The Emissaries", 50, 33, "Meccan"),
        Surah(78, "النبأ", "An-Naba", "The Tidings", 40, 80, "Meccan"),
        Surah(79, "النازعات", "An-Nazi'at", "Those Who Drag Forth", 46, 81, "Meccan"),
        Surah(80, "عبس", "Abasa", "He Frowned", 42, 24, "Meccan"),
        Surah(81, "التكوير", "At-Takwir", "The Overthrowing", 29, 7, "Meccan"),
        Surah(82, "الانفطار", "Al-Infitar", "The Cleaving", 19, 82, "Meccan"),
        Surah(83, "المطففين", "Al-Mutaffifin", "Defrauding", 36, 86, "Meccan"),
        Surah(84, "الانشقاق", "Al-Inshiqaq", "The Sundering", 25, 83, "Meccan"),
        Surah(85, "البروج", "Al-Buruj", "The Mansions of the Stars", 22, 27, "Meccan"),
        Surah(86, "الطارق", "At-Tariq", "The Morning Star", 17, 36, "Meccan"),
        Surah(87, "الأعلى", "Al-A'la", "The Most High", 19, 8, "Meccan"),
        Surah(88, "الغاشية", "Al-Ghashiyah", "The Overwhelming", 26, 68, "Meccan"),
        Surah(89, "الفجر", "Al-Fajr", "The Dawn", 30, 10, "Meccan"),
        Surah(90, "البلد", "Al-Balad", "The City", 20, 35, "Meccan"),
        Surah(91, "الشمس", "As-Shams", "The Sun", 15, 26, "Meccan"),
        Surah(92, "الليل", "Al-Lail", "The Night", 21, 9, "Meccan"),
        Surah(93, "الضحى", "Ad-Duha", "The Morning Hours", 11, 11, "Meccan"),
        Surah(94, "الشرح", "As-Sharh", "The Consolation", 8, 12, "Meccan"),
        Surah(95, "التين", "At-Tin", "The Fig", 8, 28, "Meccan"),
        Surah(96, "العلق", "Al-Alaq", "The Clot", 19, 1, "Meccan"),
        Surah(97, "القدر", "Al-Qadr", "The Power", 5, 25, "Meccan"),
        Surah(98, "البينة", "Al-Bayyinah", "The Clear Proof", 8, 100, "Medinan"),
        Surah(99, "الزلزلة", "Az-Zalzalah", "The Earthquake", 8, 93, "Medinan"),
        Surah(100, "العاديات", "Al-Adiyat", "The Courser", 11, 14, "Meccan"),
        Surah(101, "القارعة", "Al-Qari'ah", "The Calamity", 11, 30, "Meccan"),
        Surah(102, "التكاثر", "At-Tathur", "The Rivalry in World Increase", 8, 16, "Meccan"),
        Surah(103, "العصر", "Al-Asr", "The Declining Day", 3, 13, "Meccan"),
        Surah(104, "الهمزة", "Al-Humazah", "The Traducer", 9, 32, "Meccan"),
        Surah(105, "الفيل", "Al-Fil", "The Elephant", 5, 19, "Meccan"),
        Surah(106, "قريش", "Quraysh", "Quraysh", 4, 29, "Meccan"),
        Surah(107, "الماعون", "Al-Ma'un", "The Small Kindnesses", 7, 17, "Meccan"),
        Surah(108, "الكوثر", "Al-Kawthar", "The Abundance", 3, 15, "Meccan"),
        Surah(109, "الكافرون", "Al-Kafirun", "The Disbelievers", 6, 18, "Meccan"),
        Surah(110, "النصر", "An-Nasr", "The Divine Support", 3, 114, "Medinan"),
        Surah(111, "المسد", "Al-Masad", "The Palm Fiber", 5, 6, "Meccan"),
        Surah(112, "الإخلاص", "Al-Ikhlas", "The Sincerity", 4, 22, "Meccan"),
        Surah(113, "الفلق", "Al-Falaq", "The Daybreak", 5, 20, "Meccan"),
        Surah(114, "الناس", "An-Nas", "Mankind", 6, 21, "Meccan")
    )

    fun getSurah(index: Int): Surah {
        return surahs.firstOrNull { it.index == index } ?: surahs[0]
    }

    /**
     * Retrieve 28x28 matrix for a given Surah.
     * Uses hybrid strategy: opens matrices.json, if not found, falls back on deterministic metadata generator.
     */
    fun getMatrix(context: Context, surahIndex: Int): Array<DoubleArray> {
        val size = 28
        val defaultMatrix = Array(size) { DoubleArray(size) }
        
        try {
            val jsonStream: InputStream = context.assets.open("matrices.json")
            val sizeAvailable = jsonStream.available()
            val buffer = ByteArray(sizeAvailable)
            jsonStream.read(buffer)
            jsonStream.close()
            
            val jsonStr = String(buffer, Charsets.UTF_8)
            val jsonObject = JSONObject(jsonStr)
            val key = surahIndex.toString()
            if (jsonObject.has(key)) {
                val jsonArray = jsonObject.getJSONArray(key)
                val matrix = Array(size) { DoubleArray(size) }
                for (i in 0 until size) {
                    val rArray = jsonArray.getJSONArray(i)
                    for (j in 0 until size) {
                        matrix[i][j] = rArray.getDouble(j)
                    }
                }
                return matrix
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Deterministic Fallback Generator
        val surah = getSurah(surahIndex)
        val seed = (surahIndex * 31 + surah.totalVerses * 17 + surah.revelationOrder).toLong()
        val random = java.util.Random(seed)
        
        // Probability of cell activation scales with verses count
        // Al-Baqarah (286 verses) -> highly dense, Al-Kawthar (3 verses) -> highly sparse
        val activationProbability = 0.05 + 0.85 * (1.0 - kotlin.math.exp(-surah.totalVerses / 28.0))
        val maxScale = 1.0 + surah.totalVerses.toDouble() / 12.0
        
        val matrix = Array(size) { i ->
            DoubleArray(size) { j ->
                if (random.nextDouble() < activationProbability) {
                    val angle = (i * surahIndex + j * 17.0)
                    val sinePattern = 0.5 * (1.0 + sin(angle))
                    val baseValue = random.nextDouble() * maxScale * sinePattern
                    val rounded = kotlin.math.round(baseValue)
                    if (rounded < 1.0) 1.0 else rounded
                } else {
                    0.0
                }
            }
        }
        return matrix
    }
}
