package net.inveed.commons;

import java.util.ArrayList;
import java.util.HashMap;

import net.inveed.commons.annotations.EnumDisplayValue;

public enum Language {
	UNKNOWN("Unknown", "Unknown", "None", "", "", "", "Unknown"),
	ABKHAZ ("Abkhaz", "Abkhaz", "Northwest Caucasian", "ab", "abk", "abk", "аҧсуа бызшәа, аҧсшәа"),
	AFAR ("Afar", "Afar", "Afro-Asiatic", "aa", "aar", "aar", "Afaraf"),
	AFRIKAANS ("Afrikaans", "Afrikaans", "Indo-European", "af", "afr", "afr", "Afrikaans"),
	AKAN ("Akan", "Akan", "Niger–Congo", "ak", "aka", "aka", "Akan"),
	ALBANIAN ("Albanian", "Albanian", "Indo-European", "sq", "sqi", "alb", "Shqip"),
	AMHARIC ("Amharic", "Amharic", "Afro-Asiatic", "am", "amh", "amh", "አማርኛ"),
	ARABIC ("Arabic", "Arabic", "Afro-Asiatic", "ar", "ara", "ara", "العربية"),
	ARAGONESE ("Aragonese", "Aragonese", "Indo-European", "an", "arg", "arg", "aragonés"),
	ARMENIAN ("Armenian", "Armenian", "Indo-European", "hy", "hye", "arm", "Հայերեն"),
	ASSAMESE ("Assamese", "Assamese", "Indo-European", "as", "asm", "asm", "অসমীয়া"),
	AUSTRALIAN ("Australian", "Australian", "Indo-European", "ac", "ast", "ast", "Ástralic"),
	AVARIC ("Avaric", "Avaric", "Northeast Caucasian", "av", "ava", "ava", "авар мацӀ, магӀарул мацӀ"),
	AVESTAN ("Avestan", "Avestan", "Indo-European", "ae", "ave", "ave", "avesta"),
	AYMARA ("Aymara", "Aymara", "Aymaran", "ay", "aym", "aym", "aymar aru"),
	AZERBAIJANI ("Azerbaijani", "Azerbaijani", "Turkic", "az", "aze", "aze", "azərbaycan dili"),
	BAMBARA ("Bambara", "Bambara", "Niger–Congo", "bm", "bam", "bam", "bamanankan"),
	BASHKIR ("Bashkir", "Bashkir", "Turkic", "ba", "bak", "bak", "башҡорт теле"),
	BASQUE ("Basque", "Basque", "Language isolate", "eu", "eus", "baq", "euskara, euskera"),
	BELARUSIAN ("Belarusian", "Belarusian", "Indo-European", "be", "bel", "bel", "беларуская мова"),
	BENGALI ("Bengali", "Bengali, Bangla", "Indo-European", "bn", "ben", "ben", "বাংলা"),
	BIHARI ("Bihari", "Bihari", "Indo-European", "bh", "bih", "bih", "भोजपुरी"),
	BISLAMA ("Bislama", "Bislama", "Creole", "bi", "bis", "bis", "Bislama"),
	BOSNIAN ("Bosnian", "Bosnian", "Indo-European", "bs", "bos", "bos", "bosanski jezik"),
	BRETON ("Breton", "Breton", "Indo-European", "br", "bre", "bre", "brezhoneg"),
	BULGARIAN ("Bulgarian", "Bulgarian", "Indo-European", "bg", "bul", "bul", "български език"),
	BURMESE ("Burmese", "Burmese", "Sino-Tibetan", "my", "mya", "bur", "ဗမာစာ"),
	CATALAN ("Catalan", "Catalan", "Indo-European", "ca", "cat", "cat", "català"),
	CHAMORRO ("Chamorro", "Chamorro", "Austronesian", "ch", "cha", "cha", "Chamoru"),
	CHECHEN ("Chechen", "Chechen", "Northeast Caucasian", "ce", "che", "che", "нохчийн мотт"),
	CHICHEWA ("Chichewa", "Chichewa", "Niger–Congo", "ny", "nya", "nya", "chiCheŵa, chinyanja"),
	CHINESE ("Chinese", "Chinese", "Sino-Tibetan", "zh", "zho", "chi", "中文, Zhōngwén, 汉语, 漢語"),
	CHUVASH ("Chuvash", "Chuvash", "Turkic", "cv", "chv", "chv", "чӑваш чӗлхи"),
	CORNISH ("Cornish", "Cornish", "Indo-European", "kw", "cor", "cor", "Kernewek"),
	CORSICAN ("Corsican", "Corsican", "Indo-European", "co", "cos", "cos", "corsu, lingua corsa"),
	CREE ("Cree", "Cree", "Algonquian", "cr", "cre", "cre", "ᓀᐦᐃᔭᐍᐏᐣ"),
	CROATIAN ("Croatian", "Croatian", "Indo-European", "hr", "hrv", "hrv", "hrvatski jezik"),
	CZECH ("Czech", "Czech", "Indo-European", "cs", "ces", "cze", "čeština, český jazyk"),
	DANISH ("Danish", "Danish", "Indo-European", "da", "dan", "dan", "dansk"),
	DIVEHI ("Divehi", "Divehi, Dhivehi, Maldivian", "Indo-European", "dv", "div", "div", "ދިވެހި"),
	DUTCH ("Dutch", "Dutch", "Indo-European", "nl", "nld", "dut", "Nederlands, Vlaams"),
	DZONGKHA ("Dzongkha", "Dzongkha", "Sino-Tibetan", "dz", "dzo", "dzo", "རྫོང་ཁ"),
	ENGLISH ("English", "English", "Indo-European", "en", "eng", "eng", "English"),
	ESPERANTO ("Esperanto", "Esperanto", "Constructed", "eo", "epo", "epo", "Esperanto"),
	ESTONIAN ("Estonian", "Estonian", "Uralic", "et", "est", "est", "eesti, eesti keel"),
	EWE ("Ewe", "Ewe", "Niger–Congo", "ee", "ewe", "ewe", "Eʋegbe"),
	FAROESE ("Faroese", "Faroese", "Indo-European", "fo", "fao", "fao", "føroyskt"),
	FIJIAN ("Fijian", "Fijian", "Austronesian", "fj", "fij", "fij", "vosa Vakaviti"),
	FINNISH ("Finnish", "Finnish", "Uralic", "fi", "fin", "fin", "suomi, suomen kieli"),
	FRENCH ("French", "French", "Indo-European", "fr", "fra", "fre", "français, langue française"),
	FULA ("Fula", "Fula, Fulah, Pulaar, Pular", "Niger–Congo", "ff", "ful", "ful", "Fulfulde, Pulaar, Pular"),
	GALICIAN ("Galician", "Galician", "Indo-European", "gl", "glg", "glg", "galego"),
	GEORGIAN ("Georgian", "Georgian", "South Caucasian", "ka", "kat", "geo", "ქართული"),
	GERMAN ("German", "German", "Indo-European", "de", "deu", "ger", "Deutsch"),
	GREEK ("Greek", "Greek", "Indo-European", "el", "ell", "gre", "ελληνικά"),
	GUARAN("Guaraní", "Guaraní", "Tupian", "gn", "grn", "grn", "Avañe'ẽ"),
	GUJARATI ("Gujarati", "Gujarati", "Indo-European", "gu", "guj", "guj", "ગુજરાતી"),
	HAITIAN ("Haitian", "Haitian, Haitian Creole", "Creole", "ht", "hat", "hat", "Kreyòl ayisyen"),
	HAUSA ("Hausa", "Hausa", "Afro-Asiatic", "ha", "hau", "hau", "Hausa, هَوُسَ"),
	HEBREW ("Hebrew", "Hebrew", "Afro-Asiatic", "he", "heb", "heb", "עברית"),
	HERERO ("Herero", "Herero", "Niger–Congo", "hz", "her", "her", "Otjiherero"),
	HINDI ("Hindi", "Hindi", "Indo-European", "hi", "hin", "hin", "हिन्दी, हिंदी"),
	HIRI ("Hiri Motu", "Hiri Motu", "Austronesian", "ho", "hmo", "hmo", "Hiri Motu"),
	HUNGARIAN ("Hungarian", "Hungarian", "Uralic", "hu", "hun", "hun", "magyar"),
	INTERLINGUA ("Interlingua", "Interlingua", "Constructed", "ia", "ina", "ina", "Interlingua"),
	INDONESIAN ("Indonesian", "Indonesian", "Austronesian", "id", "ind", "ind", "Bahasa Indonesia"),
	INTERLINGUE ("Interlingue", "Interlingue", "Constructed", "ie", "ile", "ile", "Originally called Occidental; then Interlingue after WWII"),
	IRISH ("Irish", "Irish", "Indo-European", "ga", "gle", "gle", "Gaeilge"),
	IGBO ("Igbo", "Igbo", "Niger–Congo", "ig", "ibo", "ibo", "Asụsụ Igbo"),
	INUPIAQ ("Inupiaq", "Inupiaq", "Eskimo–Aleut", "ik", "ipk", "ipk", "Iñupiaq, Iñupiatun"),
	IDO ("Ido", "Ido", "Constructed", "io", "ido", "ido", "Ido"),
	ICELANDIC ("Icelandic", "Icelandic", "Indo-European", "is", "isl", "ice", "Íslenska"),
	ITALIAN ("Italian", "Italian", "Indo-European", "it", "ita", "ita", "italiano"),
	INUKTITUT ("Inuktitut", "Inuktitut", "Eskimo–Aleut", "iu", "iku", "iku", "ᐃᓄᒃᑎᑐᑦ"),
	JAPANESE ("Japanese", "Japanese", "Japonic", "ja", "jpn", "jpn", "日本語 ,にほんご"),
	JAVANESE ("Javanese", "Javanese", "Austronesian", "jv", "jav", "jav", "basa Jawa"),
	KALAALLISUT ("Kalaallisut", "Kalaallisut, Greenlandic", "Eskimo–Aleut", "kl", "kal", "kal", "kalaallisut, kalaallit oqaasii"),
	KANNADA ("Kannada", "Kannada", "Dravidian", "kn", "kan", "kan", "ಕನ್ನಡ"),
	KANURI ("Kanuri", "Kanuri", "Nilo-Saharan", "kr", "kau", "kau", "Kanuri"),
	KASHMIRI ("Kashmiri", "Kashmiri", "Indo-European", "ks", "kas", "kas", "कश्मीरी, كشميري‎"),
	KAZAKH ("Kazakh", "Kazakh", "Turkic", "kk", "kaz", "kaz", "қазақ тілі"),
	KHMER ("Khmer", "Khmer", "Austroasiatic", "km", "khm", "khm", "ខ្មែរ, ខេមរភាសា, ភាសាខ្មែរ"),
	KIKUYU ("Kikuyu", "Kikuyu, Gikuyu", "Niger–Congo", "ki", "kik", "kik", "Gĩkũyũ"),
	KINYARWANDA ("Kinyarwanda", "Kinyarwanda", "Niger–Congo", "rw", "kin", "kin", "Ikinyarwanda"),
	KYRGYZ ("Kyrgyz", "Kyrgyz", "Turkic", "ky", "kir", "kir", "Кыргызча, Кыргыз тили"),
	KOMI ("Komi", "Komi", "Uralic", "kv", "kom", "kom", "коми кыв"),
	KONGO ("Kongo", "Kongo", "Niger–Congo", "kg", "kon", "kon", "Kikongo"),
	KOREAN ("Korean", "Korean", "Koreanic", "ko", "kor", "kor", "한국어, 조선어"),
	KURDISH ("Kurdish", "Kurdish", "Indo-European", "ku", "kur", "kur", "Kurdî, كوردی‎"),
	KWANYAMA ("Kwanyama", "Kwanyama, Kuanyama", "Niger–Congo", "kj", "kua", "kua", "Kuanyama"),
	LATIN ("Latin", "Latin", "Indo-European", "la", "lat", "lat", "latine, lingua latina"),
	LUXEMBOURGISH ("Luxembourgish", "Luxembourgish, Letzeburgesch", "Indo-European", "lb", "ltz", "ltz", "Lëtzebuergesch"),
	GANDA ("Ganda", "Ganda", "Niger–Congo", "lg", "lug", "lug", "Luganda"),
	LIMBURGISH ("Limburgish", "Limburgish, Limburgan, Limburger", "Indo-European", "li", "lim", "lim", "Limburgs"),
	LINGALA ("Lingala", "Lingala", "Niger–Congo", "ln", "lin", "lin", "Lingála"),
	LAO ("Lao", "Lao", "Tai–Kadai", "lo", "lao", "lao", "ພາສາລາວ"),
	LITHUANIAN ("Lithuanian", "Lithuanian", "Indo-European", "lt", "lit", "lit", "lietuvių kalba"),
	LUBA_KATANGA ("Luba-Katanga", "Luba-Katanga", "Niger–Congo", "lu", "lub", "lub", "Tshiluba"),
	LATVIAN ("Latvian", "Latvian", "Indo-European", "lv", "lav", "lav", "latviešu valoda"),
	MANX ("Manx", "Manx", "Indo-European", "gv", "glv", "glv", "Gaelg, Gailck"),
	MACEDONIAN ("Macedonian", "Macedonian", "Indo-European", "mk", "mkd", "mac", "македонски јазик"),
	MALAGASY ("Malagasy", "Malagasy", "Austronesian", "mg", "mlg", "mlg", "fiteny malagasy"),
	MALAY ("Malay", "Malay", "Austronesian", "ms", "msa", "may", "bahasa Melayu, بهاس ملايو‎"),
	MALAYALAM ("Malayalam", "Malayalam", "Dravidian", "ml", "mal", "mal", "മലയാളം"),
	MALTESE ("Maltese", "Maltese", "Afro-Asiatic", "mt", "mlt", "mlt", "Malti"),
	MAORI ("Māori", "Māori", "Austronesian", "mi", "mri", "mao", "te reo Māori"),
	MARATHI ("Marathi", "Marathi, Marāṭhī", "Indo-European", "mr", "mar", "mar", "मराठी"),
	MARSHALLESE ("Marshallese", "Marshallese", "Austronesian", "mh", "mah", "mah", "Kajin M̧ajeļ"),
	MONGOLIAN ("Mongolian", "Mongolian", "Mongolic", "mn", "mon", "mon", "Монгол хэл"),
	NAURU ("Nauru", "Nauru", "Austronesian", "na", "nau", "nau", "Ekakairũ Naoero"),
	NAVAJO ("Navajo", "Navajo, Navaho", "Dené–Yeniseian", "nv", "nav", "nav", "Diné bizaad"),
	NOTHERN_NDEBELE ("Northern Ndebele", "Northern Ndebele", "Niger–Congo", "nd", "nde", "nde", "isiNdebele"),
	NEPALI ("Nepali", "Nepali", "Indo-European", "ne", "nep", "nep", "नेपाली"),
	NDONGA ("Ndonga", "Ndonga", "Niger–Congo", "ng", "ndo", "ndo", "Owambo"),
	NORWEGIAN_BOKMAL ("Norwegian Bokmål", "Norwegian Bokmål", "Indo-European", "nb", "nob", "nob", "Norsk bokmål"),
	NORWEGIAN_NYNORSK ("Norwegian Nynorsk", "Norwegian Nynorsk", "Indo-European", "nn", "nno", "nno", "Norsk nynorsk"),
	NORWEGIAN ("Norwegian", "Norwegian", "Indo-European", "no", "nor", "nor", "Norsk"),
	NUOSU ("Nuosu", "Nuosu", "Sino-Tibetan", "ii", "iii", "iii", "ꆈꌠ꒿ Nuosuhxop"),
	SOUTHERN_NDEBELE ("Southern Ndebele", "Southern Ndebele", "Niger–Congo", "nr", "nbl", "nbl", "isiNdebele"),
	OCCITAN ("Occitan", "Occitan", "Indo-European", "oc", "oci", "oci", "occitan, lenga d'òc"),
	OJIBWE ("Ojibwe", "Ojibwe, Ojibwa", "Algonquian", "oj", "oji", "oji", "ᐊᓂᔑᓈᐯᒧᐎᓐ"),
	OLD_CHURCH_SLAVONIC ("Old Church Slavonic", "Old Church Slavonic, Church Slavonic, Old Bulgarian", "Indo-European", "cu", "chu", "chu", "ѩзыкъ словѣньскъ"),
	OROMO ("Oromo", "Oromo", "Afro-Asiatic", "om", "orm", "orm", "Afaan Oromoo"),
	ORIYA ("Oriya", "Oriya", "Indo-European", "or", "ori", "ori", "ଓଡ଼ିଆ"),
	OSSETIAN ("Ossetian", "Ossetian, Ossetic", "Indo-European", "os", "oss", "oss", "ирон æвзаг"),
	PANJABI ("Panjabi", "Panjabi, Punjabi", "Indo-European", "pa", "pan", "pan", "ਪੰਜਾਬੀ, پنجابی‎"),
	PALI ("Pāli", "Pāli", "Indo-European", "pi", "pli", "pli", "पाऴि"),
	PERSIAN ("Persian", "Persian,Farsi", "Indo-European", "fa", "fas", "per", "فارسی"),
	POLISH ("Polish", "Polish", "Indo-European", "pl", "pol", "pol", "język polski, polszczyzna"),
	PASHTO ("Pashto", "Pashto, Pushto", "Indo-European", "ps", "pus", "pus", "پښتو"),
	PORTUGUESE ("Portuguese", "Portuguese", "Indo-European", "pt", "por", "por", "português"),
	QUECHUA ("Quechua", "Quechua", "Quechuan", "qu", "que", "que", "Runa Simi, Kichwa"),
	ROMANSH ("Romansh", "Romansh", "Indo-European", "rm", "roh", "roh", "rumantsch grischun"),
	KIRUNDI ("Kirundi", "Kirundi", "Niger–Congo", "rn", "run", "run", "Ikirundi"),
	ROMANIAN ("Romanian", "Romanian", "Indo-European", "ro", "ron", "rum", "limba română"),
	RUSSIAN ("Russian", "Russian", "Indo-European", "ru", "rus", "rus", "Русский"),
	SANSKRIT ("Sanskrit", "Sanskrit, Saṁskṛta", "Indo-European", "sa", "san", "san", "संस्कृतम्"),
	SARDINIAN ("Sardinian", "Sardinian", "Indo-European", "sc", "srd", "srd", "sardu"),
	SINDHI ("Sindhi", "Sindhi", "Indo-European", "sd", "snd", "snd", "सिन्धी, سنڌي، سندھی‎"),
	NORTHERN ("Northern Sami", "Northern Sami", "Uralic", "se", "sme", "sme", "Davvisámegiella"),
	SAMOAN ("Samoan", "Samoan", "Austronesian", "sm", "smo", "smo", "gagana fa'a Samoa"),
	SANGO ("Sango", "Sango", "Creole", "sg", "sag", "sag", "yângâ tî sängö"),
	SERBIAN ("Serbian", "Serbian", "Indo-European", "sr", "srp", "srp", "српски језик"),
	SCOTTISH ("Scottish Gaelic", "Scottish Gaelic, Gaelic", "Indo-European", "gd", "gla", "gla", "Gàidhlig"),
	SHONA ("Shona", "Shona", "Niger–Congo", "sn", "sna", "sna", "chiShona"),
	SINHALA ("Sinhala", "Sinhala, Sinhalese", "Indo-European", "si", "sin", "sin", "සිංහල"),
	SLOVAK ("Slovak", "Slovak", "Indo-European", "sk", "slk", "slo", "slovenčina, slovenský jazyk"),
	SLOVENE ("Slovene", "Slovene", "Indo-European", "sl", "slv", "slv", "slovenski jezik, slovenščina"),
	SOMALI ("Somali", "Somali", "Afro-Asiatic", "so", "som", "som", "Soomaaliga, af Soomaali"),
	SOUTHERN ("Southern Sotho", "Southern Sotho", "Niger–Congo", "st", "sot", "sot", "Sesotho"),
	SPANISH ("Spanish", "Spanish", "Indo-European", "es", "spa", "spa", "español"),
	SUNDANESE ("Sundanese", "Sundanese", "Austronesian", "su", "sun", "sun", "Basa Sunda"),
	SWAHILI ("Swahili", "Swahili", "Niger–Congo", "sw", "swa", "swa", "Kiswahili"),
	SWATI ("Swati", "Swati", "Niger–Congo", "ss", "ssw", "ssw", "SiSwati"),
	SWEDISH ("Swedish", "Swedish", "Indo-European", "sv", "swe", "swe", "svenska"),
	TAMIL ("Tamil", "Tamil", "Dravidian", "ta", "tam", "tam", "தமிழ்"),
	TELUGU ("Telugu", "Telugu", "Dravidian", "te", "tel", "tel", "తెలుగు"),
	TAJIK ("Tajik", "Tajik", "Indo-European", "tg", "tgk", "tgk", "тоҷикӣ, toçikī, تاجیکی‎"),
	THAI ("Thai", "Thai", "Tai–Kadai", "th", "tha", "tha", "ไทย"),
	TIGRINYA ("Tigrinya", "Tigrinya", "Afro-Asiatic", "ti", "tir", "tir", "ትግርኛ"),
	TIBETAN ("Tibetan Standard", "Tibetan Standard, Tibetan, Central", "Sino-Tibetan", "bo", "bod", "tib", "བོད་ཡིག"),
	TURKMEN ("Turkmen", "Turkmen", "Turkic", "tk", "tuk", "tuk", "Türkmen, Түркмен"),
	TAGALOG ("Tagalog", "Tagalog", "Austronesian", "tl", "tgl", "tgl", "Wikang Tagalog, ᜏᜒᜃᜅ᜔ ᜆᜄᜎᜓᜄ᜔"),
	TSWANA ("Tswana", "Tswana", "Niger–Congo", "tn", "tsn", "tsn", "Setswana"),
	TONGA ("Tonga ", "Tonga , Tonga Islands", "Austronesian", "to", "ton", "ton", "faka Tonga"),
	TURKISH ("Turkish", "Turkish", "Turkic", "tr", "tur", "tur", "Türkçe"),
	TSONGA ("Tsonga", "Tsonga", "Niger–Congo", "ts", "tso", "tso", "Xitsonga"),
	TATAR ("Tatar", "Tatar", "Turkic", "tt", "tat", "tat", "татар теле, tatar tele"),
	TWI ("Twi", "Twi", "Niger–Congo", "tw", "twi", "twi", "Twi"),
	TAHITIAN ("Tahitian", "Tahitian", "Austronesian", "ty", "tah", "tah", "Reo Tahiti"),
	UYGHUR ("Uyghur", "Uyghur", "Turkic", "ug", "uig", "uig", "ئۇيغۇرچە‎, Uyghurche"),
	UKRAINIAN ("Ukrainian", "Ukrainian", "Indo-European", "uk", "ukr", "ukr", "українська мова"),
	URDU ("Urdu", "Urdu", "Indo-European", "ur", "urd", "urd", "اردو"),
	UZBEK ("Uzbek", "Uzbek", "Turkic", "uz", "uzb", "uzb", "Oʻzbek, Ўзбек, أۇزبېك‎"),
	VENDA ("Venda", "Venda", "Niger–Congo", "ve", "ven", "ven", "Tshivenḓa"),
	VIETNAMESE ("Vietnamese", "Vietnamese", "Austroasiatic", "vi", "vie", "vie", "Việt Nam"),
	VOLAPUK ("Volapük", "Volapük", "Constructed", "vo", "vol", "vol", "Volapük"),
	WALLOON ("Walloon", "Walloon", "Indo-European", "wa", "wln", "wln", "walon"),
	WELSH ("Welsh", "Welsh", "Indo-European", "cy", "cym", "wel", "Cymraeg"),
	WOLOF ("Wolof", "Wolof", "Niger–Congo", "wo", "wol", "wol", "Wollof"),
	WESTERN ("Western Frisian", "Western Frisian", "Indo-European", "fy", "fry", "fry", "Frysk"),
	XHOSA ("Xhosa", "Xhosa", "Niger–Congo", "xh", "xho", "xho", "isiXhosa"),
	YIDDISH ("Yiddish", "Yiddish", "Indo-European", "yi", "yid", "yid", "ייִדיש"),
	YORUBA ("Yoruba", "Yoruba", "Niger–Congo", "yo", "yor", "yor", "Yorùbá"),
	ZHUANG ("Zhuang", "Zhuang, Chuang", "Tai–Kadai", "za", "zha", "zha", "Saɯ cueŋƅ, Saw cuengh"),
	ZULU ("Zulu", "Zulu", "Niger–Congo", "zu", "zul", "zul", "isiZulu");

	public final String name;
	public final String nameFull;
	public final String family;
	public final String iso639_1;
	public final String iso639_2t;
	public final String iso639_2b;
	public final String nameNative;
	
	@EnumDisplayValue
	public final String getDisplayValue() {
		return this.nameFull + "/" + this.nameNative;
	}
	
	private static HashMap<String, Language> iso639_1_map;
	private static HashMap<String, Language> iso639_2b_map;
	private static HashMap<String, Language> iso639_2t_map;
	private static HashMap<String, Language> name_map;
	
	private Language(String langNameSingle, String langNameEng, String langFamily, String langCodeISO6391, String langCodeISO6392T, String langCodeISO6392B, String langNameNative) {
		this.name = langNameSingle;
		this.nameFull = langNameEng;
		this.nameNative = langNameNative;
		this.family = langFamily;
		this.iso639_1 = langCodeISO6391.toLowerCase().trim();
		this.iso639_2b = langCodeISO6392B.toLowerCase().trim();
		this.iso639_2t = langCodeISO6392T.toLowerCase().trim();
		if (this.iso639_1.length() > 0) {
			this.registerNames();
		}
	}

	private void registerNames() {
		if (iso639_1_map == null) {
			iso639_1_map = new HashMap<>();
			iso639_2b_map = new HashMap<>();
			iso639_2t_map = new HashMap<>();
			name_map = new HashMap<>();
		}
		iso639_1_map.put(this.iso639_1, this);
		iso639_2b_map.put(this.iso639_2b, this);
		iso639_2t_map.put(this.iso639_2t, this);
		
		ArrayList<String> names = new ArrayList<String>();
		this.addToNames(names, this.nameFull);
		this.addToNames(names, this.nameNative);
		
		for (String n : names) {
			if (name_map.containsKey(n)) {
				System.err.println("Duplicate name '" + n + "' defined for " + this + " and " + name_map.get(n));
			} else 
				name_map.put(n, this);
		}
	}
	
	private void addToNames(ArrayList<String> names, String name) {
		String[] na = name.split(",");
		for (String n : na) {
			String k = n.toLowerCase().trim();
			if (k.length() < 1)
				continue;
			if (!names.contains(k)) {
				names.add(k);
			}
		}
	}
	
	public static Language getByISO639_1(String code) {
		if (code == null)
			throw new NullPointerException("code");
		code = code.trim().toLowerCase();
		Language ret = iso639_1_map.get(code);
		if (ret == null)
			ret = UNKNOWN;
		return ret;
	}
	
	public static Language getByISO639_2B(String code) {
		if (code == null)
			throw new NullPointerException("code");
		code = code.trim().toLowerCase();
		Language ret = iso639_2b_map.get(code);
		if (ret == null)
			ret = UNKNOWN;
		return ret;
	}
	
	public static Language getByISO639_2T(String code) {
		if (code == null)
			throw new NullPointerException("code");
		code = code.trim().toLowerCase();
		Language ret = iso639_2t_map.get(code);
		if (ret == null)
			ret = UNKNOWN;
		return ret;
	}
	
	public static Language getByName(String name) {
		if (name == null)
			throw new NullPointerException("name");
		name = name.toLowerCase().trim();
		Language ret = name_map.get(name);
		if (ret == null)
			ret = UNKNOWN;
		return ret;
	}
	
	public static Language find(String name) {
		if (name == null)
			throw new NullPointerException("name");
		name = name.trim().toLowerCase();
		if (name.length() == 0)
			return UNKNOWN;
		
		Language ret = iso639_1_map.get(name);
		if (ret == null)
			ret = iso639_2b_map.get(name);
		if (ret == null)
			ret = iso639_2t_map.get(name);
		if (ret == null)
			ret = name_map.get(name);
		if (ret == null)
			ret = UNKNOWN;
		return ret;
	}
}
