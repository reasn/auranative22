package io.auraapp.auraandroid.common;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CuteHasher {

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private static MessageDigest mMessageDigest;

    private static MessageDigest getMessageDigest() {
        if (mMessageDigest == null) {
            try {
                mMessageDigest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return mMessageDigest;
    }

    public static String hash(String input) {
        MessageDigest messageDigest = getMessageDigest();
        messageDigest.update(input.getBytes(UTF8_CHARSET));
        byte[] result = messageDigest.digest();

        // We need 10 bits to select from 1024 symbols
        int indexA = 0;
        for (int i = 0; i < 10; i++) {

            // If lowest bit of result[i] is set, flip bit in indexA
            if ((result[i] & 1) != 0) {
                indexA += Math.pow(2, i);
            }
        }
        return EMOJI[indexA];

    }

    private static final String[] EMOJI = new String[]{
            "😀", "😁", "😂", "🤣", "😃", "😄", "😅", "😆", "😉", "😊", "😋", "😎", "😍", "😘",
            "😗", "😙", "😚", "🙂", "🤗", "🤩", "🤔", "🤨", "😐", "😑", "😶", "🙄", "😏",
            "😣", "😥", "😮", "🤐", "😯", "😪", "😫", "😴", "😌", "😛", "😜", "😝", "🤤", "😒",
            "😓", "😔", "😕", "🙃", "🤑", "😲", "🙁", "😖", "😞", "😟", "😤", "😢", "😭",
            "😦", "😧", "😨", "😩", "🤯", "😬", "😰", "😱", "😳", "🤪", "😵", "😡", "😠", "🤬",
            "😷", "🤒", "🤕", "🤢", "🤮", "🤧", "😇", "🤠", "🤡", "🤥", "🤫", "🤭", "🧐", "🤓",
            "😈", "👿", "👹", "👺", "💀", "👻", "👽", "🤖", "💩", "😺", "😸", "😹", "😻", "😼",
            "😽", "🙀", "😿", "😾",
            "👶", "👦", "👧", "👨", "👩", "👴", "👵", "👮", "🕵", "💂", "👷", "🤴", "👸", "👳",
            "👲", "🧕", "🧔", "👱", "🤵", "👰", "🤰", "🤱", "👼", "🎅", "🤶",
            "🙍", "🙎", "🙅", "🙆", "💁", "🙋", "🙇", "🤦", "🤷", "💆", "💇", "🚶", "🏃", "💃",
            "🕺", "👯", "🕴", "🗣", "👤", "👥", "👫", "👬", "👭", "💏", "💑", "👪", "🤳", "💪",
            "👈", "👉", "👆", "🖕", "👇", "🤞", "🖖", "🤘", "🖐", "✋", "👌", "👍", "👎", "✊",
            "👊", "🤛", "🤜", "🤚", "👋", "🤟", "👏", "👐", "🙌", "🤲", "🙏", "🤝", "💅", "👂",
            "👃", "👣", "👀", "👁", "🧠", "👅", "👄", "💋",
            "👓", "🕶", "👔", "👕", "👖", "🧣", "🧤", "🧥", "🧦", "👗", "👘", "👙", "👚", "👛",
            "👜", "👝", "🎒", "👞", "👟", "👠", "👡", "👢", "👑", "👒", "🎩", "🎓", "🧢", "⛑",
            "💄", "💍", "🌂", "💼",
            "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐽",
            "🐸", "🐵", "🙊", "🙉", "🙊", "🐒", "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅",
            "🦉", "🦇", "🐺", "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐚", "🐞", "🐜", "🕷",
            "🕸", "🐢", "🐍", "🦎", "🦂", "🦀", "🦑", "🐙", "🦐", "🐠", "🐟", "🐡", "🐬", "🦈",
            "🐳", "🐋", "🐊", "🐆", "🐅", "🐃", "🐂", "🐄", "🦌", "🐪", "🐫", "🐘", "🦏", "🦍",
            "🐎", "🐖", "🐐", "🐏", "🐑", "🐕", "🐩", "🐈", "🐓", "🦃", "🕊", "🐇", "🐁", "🐀",
            "🐿", "🐾", "🐉", "🐲", "🌵", "🎄", "🌲", "🌳", "🌴", "🌱", "🌿", "🍀", "🎍", "🎋",
            "🍃", "🍂", "🍁", "🍄", "🌾", "💐", "🌷", "🌹", "🥀", "🌻", "🌼", "🌸", "🌺", "🌎",
            "🌍", "🌏", "🌕", "🌖", "🌗", "🌘", "🌑", "🌒", "🌓", "🌔", "🌚", "🌝", "🌞", "🌛",
            "🌜", "🌙", "💫", "🌟", "✨", "🔥", "💥", "🌤", "🌥", "🌦", "🌈", "🌧", "⛈", "🌩",
            "🌨", "🌬", "💨", "🌪", "🌫", "🌊", "💧", "💦",
            "🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🍈", "🍒", "🍑", "🍍", "🥝",
            "🥑", "🍅", "🍆", "🥒", "🥕", "🌽", "🌶", "🥔", "🍠", "🌰", "🥜", "🍯", "🥐", "🍞",
            "🥖", "🧀", "🥚", "🍳", "🥓", "🥞", "🍤", "🍗", "🍖", "🍕", "🌭", "🍔", "🍟", "🥙",
            "🌮", "🌯", "🥗", "🥘", "🍝", "🍜", "🍲", "🍥", "🍣", "🍱", "🍛", "🍚", "🍙", "🍘",
            "🍢", "🍡", "🍧", "🍨", "🍦", "🍰", "🎂", "🍮", "🍭", "🍬", "🍫", "🍿", "🍩", "🍪",
            "🥛", "🍼", "🍵", "🍶", "🍺", "🍻", "🥂", "🍷", "🥃", "🍸", "🍹", "🍾", "🥄", "🍴",
            "🍽",
            "🏀", "🏈", "🎾", "🏐", "🏉", "🎱", "🏓", "🏸", "🥅", "🏒", "🏑", "🏏", "🏹", "🎣",
            "🥊", "🥋", "⛸", "🎿", "⛷", "🏂", "🤺", "🏄", "🏊", "🚣", "🏇", "🚴", "🚵", "🎽",
            "🏅", "🎖", "🥇", "🥈", "🥉", "🏆", "🏵", "🎗", "🎫", "🎟", "🎪", "🎭", "🎨", "🎬",
            "🎤", "🎧", "🎼", "🎹", "🥁", "🎷", "🎺", "🎸", "🎻", "🎲", "🎯", "🎳", "🎮", "🎰",
            "🚗", "🚕", "🚙", "🚌", "🚎", "🏎", "🚓", "🚑", "🚒", "🚐", "🚚", "🚛", "🚜", "🛴",
            "🚲", "🛵", "🏍", "🚨", "🚔", "🚍", "🚘", "🚖", "🚡", "🚠", "🚟", "🚃", "🚋", "🚞",
            "🚝", "🚄", "🚅", "🚈", "🚂", "🚆", "🚇", "🚊", "🚉", "🚁", "🛩", "🛫", "🛬", "🚀",
            "🛰", "💺", "🛶", "🛥", "🚤", "🛳", "⛴", "🚢", "🚧", "🚏", "🚦", "🚥", "🗺", "🗿",
            "🗽", "🗼", "🏰", "🏯", "🏟", "🎡", "🎢", "🎠", "⛱", "🏖", "🏝", "⛰", "🏔", "🗻",
            "🌋", "🏜", "🏕", "🛤", "🛣", "🏗", "🏭", "🏠", "🏡", "🏘", "🏚", "🏢", "🏬", "🏣",
            "🏤", "🏥", "🏦", "🏨", "🏪", "🏫", "🏩", "💒", "🏛", "🕌", "🕍", "🕋", "⛩", "🗾",
            "🎑", "🏞", "🌅", "🌄", "🌠", "🎇", "🎆", "🌇", "🌆", "🏙", "🌃", "🌌", "🌉", "🌁",
            "📱", "📲", "💻", "🖥", "🖨", "🖱", "🖲", "🕹", "🗜", "💽", "💾", "💿",
            "📀", "📼", "📷", "📸", "📹", "🎥", "📽", "🎞", "📞", "📟", "📠", "📺", "📻", "🎙",
            "🎚", "🎛", "⏱", "⏲", "⏰", "🕰", "⏳", "📡", "🔋", "🔌", "💡", "🔦", "🕯", "🗑",
            "🛢", "💸", "💵", "💴", "💶", "💷", "💰", "💳", "💎", "🔧", "🔨", "⚒", "🛠", "⛏",
            "🔩", "⛓", "🔫", "💣", "🔪", "🗡", "🛡", "🚬", "🏺", "🔮", "📿", "💈", "🔭", "🔬",
            "🕳", "💊", "💉", "🌡", "🚽", "🚰", "🚿", "🛁", "🛀", "🛎", "🔑", "🗝", "🚪", "🛋",
            "🛏", "🛌", "🖼", "🛍", "🛒", "🎁", "🎈", "🎏", "🎀", "🎊", "🎉", "🎎", "🏮", "🎐",
            "📩", "📨", "📧", "💌", "📥", "📤", "📦", "🏷", "📪", "📫", "📬", "📭", "📮", "📯",
            "📜", "📃", "📄", "📑", "📊", "📈", "📉", "🗒", "🗓", "📆", "📅", "📇", "🗃", "🗳",
            "🗄", "📋", "📁", "📂", "🗂", "🗞", "📰", "📓", "📔", "📒", "📕", "📗", "📘", "📙",
            "📚", "📖", "🔖", "🔗", "📎", "🖇", "📐", "📏", "📌", "📍", "📌", "🎌", "🏴", "🏁",
            "🖊", "🖋", "🖌", "🖍", "📝", "🔍", "🔎", "🔏", "🔐", "🔒", "🔓",
            "💛", "💚", "💙", "💜", "🖤", "💔", "💕", "💞", "💓", "💗", "💖", "💘",
            "💝", "💟", "🕉", "🔯", "🕎", "🛐", "⛎", "🆔", "📴", "📳", "🆚", "💮", "🆎", "🆑",
            "🆘", "❌", "🛑", "📛", "🚫", "💯", "💢", "🚷", "🚯", "🚳", "🚱", "🔞", "📵", "🚭",
            "❕", "❓", "❔", "🔅", "🔆", "🚸", "🔱", "🔰", "✅", "💹", "❎", "🌐", "💠", "🌀",
            "💤", "🏧", "🚾", "🈳", "🛂", "🛃", "🛄", "🛅", "🚹", "🚺", "🚼", "🚻", "🚮", "🎦",
            "📶", "🈁", "🔣", "🔤", "🔡", "🔠", "🆖", "🆗", "🆙", "🆒", "🆕", "🆓", "🔟", "🔢",
            "⏸", "⏯", "⏹", "⏺", "⏭", "⏮", "⏩", "⏪", "⏫", "⏬", "🔼", "🔽", "🔀", "🔁",
            "🔂", "🔄", "🔃", "🎵", "🎶", "➕", "➖", "➗", "💲", "💱", "➰", "➿", "🔚", "🔙",
            "🔛", "🔝", "🔘", "🔴", "🔵", "🔺", "🔻", "🔸", "🔹", "🔶", "🔷", "🔳", "🔲", "🔈",
            "🔇", "🔉", "🔊", "🔔", "🔕", "📣", "📢", "💬", "💭", "🗯", "🃏", "🎴", "🕐", "🕑",
            "🕒", "🕓", "🕔", "🕕", "🕖", "🕗", "🕘", "🕙", "🕚", "🕛", "🕜", "🕝", "🕞", "🕟",
            "🕠", "🕡", "🕢", "🕣", "🕤", "🕥", "🕦", "🕧", "🏴", "🏁", "🚩", "🎌",
            "A", "B", "C", "D", "E", "F", "G", "H", "J", "I", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "#", "+", "!", "$", "%", "&", "=", "?", "@", ">", "<", "*", "^"
    };


//    /**
//     * www.gingersoftware.com/content/grammar-rules/adjectives/lists-of-adjectives/
//     */
//    public static final String[] adjectives = new String[]{
//            "abandoned", "aberrant", "abhorrent", "abiding", "abject", "ablaze", "able", "abounding", "abrasive", "abrupt", "absent", "absorbed", "absorbing", "abstracted", "absurd", "acceptable", "accessible", "accidental", "accurate", "acid", "acidic", "acoustic", "acrid", "actually", "adamant", "adaptable", "adhesive", "adjoining", "adorable", "adventurous", "afraid", "aggressive", "agonizing", "agreeable", "ahead", "ajar", "alcoholic", "alert", "alike", "alive", "alleged", "alluring", "aloof", "amazing", "ambiguous", "ambitious", "amuck", "amused", "amusing", "ancient", "angry", "animated", "annoyed", "annoying", "anxious", "apathetic", "aquatic", "aromatic", "arrogant", "ashamed", "aspiring", "assorted", "astonishing", "attractive", "auspicious", "automatic", "available", "average", "awake", "aware", "awesome", "axiomatic",
//            "barbarous", "bashful", "bawdy", "beautiful", "befitting", "belligerent", "beneficial", "berserk", "best", "better", "bewildered", "big", "billowy", "bite-sized", "bitter", "bizarre", "black", "black-and-white", "bloody", "blue-eyed", "blushing", "boiling", "boorish", "bored", "bouncy", "boundless", "brainy", "brash", "brave", "brawny", "breakable", "breezy", "brief", "bright", "broad", "bumpy", "burly", "bustling", "busy",
//            "cagey", "calculating", "callous", "calm", "capable", "capricious", "careful", "careless", "caring", "cautious", "ceaseless", "certain", "changeable", "charming", "cheerful", "chemical", "chief", "childlike", "chilly", "chivalrous", "chubby", "chunky", "clammy", "classy", "clean", "clear", "clever", "cloistered", "cloudy", "closed", "clumsy", "cluttered", "coherent", "cold", "colorful", "colossal", "combative", "comfortable", "common", "complete", "complex", "concerned", "condemned", "confused", "conscious", "cooing", "cool", "cooperative", "coordinated", "courageous", "cowardly", "crabby", "craven", "crazy", "creepy", "crooked", "crowded", "cruel", "cuddly", "cultured", "cumbersome", "curious", "curly", "curved", "curvy", "cute", "cynical",
//            "daffy", "daily", "damaged", "damaging", "damp", "dangerous", "dapper", "dark", "dashing", "dazzling", "deafening", "debonair", "decisive", "decorous", "deep", "deeply", "defeated", "defective", "defiant", "delicate", "delicious", "delightful", "demonic", "delirious", "descriptive", "deserted", "detailed", "determined", "devilish", "didactic", "difficult", "diligent", "direful", "disagreeable", "discreet", "disillusioned", "dispensable", "distinct", "disturbed", "divergent", "dizzy", "domineering", "doubtful", "drab", "draconian", "dramatic", "dreary", "drunk", "dry", "dull", "dusty", "dusty", "dynamic", "dysfunctional",
//            "eager", "early", "earsplitting", "earthy", "easy", "eatable", "economic", "educated", "efficacious", "efficient", "elastic", "elated", "elderly", "electric", "elegant", "elfin", "elite", "embarrassed", "eminent", "enchanted", "enchanting", "encouraging", "endurable", "energetic", "enormous", "entertaining", "enthusiastic", "envious", "equable", "ethereal", "evanescent", "evasive", "even", "excellent", "excited", "exciting", "exclusive", "exotic", "expensive", "extra-large", "extra-small", "exuberant", "exultant",
//            "fabulous", "fair", "faithful", "fallacious", "familiar", "famous", "fanatical", "fancy", "fantastic", "fascinated", "fast", "faulty", "fearful", "fearless", "feeble", "feigned", "fertile", "festive", "fierce", "filthy", "fine", "finicky", "first", "fixed", "flagrant", "flaky", "flashy", "flat", "flawless", "flimsy", "flippant", "flowery", "fluffy", "fluttering", "foamy", "foolish", "foregoing", "forgetful", "fortunate", "frail", "fragile", "frantic", "free", "freezing", "frequent", "fresh", "fretful", "friendly", "frightened", "frightening", "full", "fumbling", "functional", "funny", "furry", "furtive", "future", "futuristic", "fuzzy",
//            "gabby", "gainful", "gamy", "gaping", "garrulous", "gaudy", "gentle", "giant", "giddy", "gifted", "gigantic", "glamorous", "gleaming", "glib", "glistening", "glorious", "glossy", "godly", "good", "goofy", "gorgeous", "graceful", "grandiose", "grateful", "gray", "greasy", "great", "greedy", "green", "grey", "groovy", "grotesque", "grouchy", "grubby", "grumpy", "guarded", "guiltless", "gullible", "gusty", "guttural",
//            "habitual", "hallowed", "halting", "handsome", "handsomely", "handy", "hanging", "happy", "hard", "hard-to-find", "harmonious", "harsh", "heady", "healthy", "heartbreaking", "heavenly", "heavy", "hellish", "helpful", "helpless", "hesitant", "hideous", "high", "highfalutin", "high-pitched", "hilarious", "hissing", "historical", "holistic", "hollow", "homeless", "homely", "honorable", "horrible", "hospitable", "hot", "huge", "hulking", "humdrum", "humorous", "hungry", "hurried", "hurt", "hushed", "husky", "hypnotic", "hysterical",
//            "icky", "icy", "ignorant", "illustrious", "imaginary", "impolite", "important", "impossible", "incandescent", "inconclusive", "industrious", "incredible", "inexpensive", "infamous", "innate", "innocent", "inquisitive", "insidious", "instinctive", "intelligent", "interesting", "internal", "invincible", "irritating", "itchy",
//            "jaded", "jagged", "jazzy", "jealous", "jittery", "jolly", "joyous", "judicious", "juicy", "jumbled", "jumpy", "juvenile",
//            "keen", "kind", "kindhearted", "kindly", "knotty", "knowing", "knowledgeable",
//            "lackadaisical", "lacking", "lame", "lamentable", "languid", "large", "laughable", "lavish", "lazy", "lean", "learned", "left", "legal", "lethal", "lewd", "light", "like", "likeable", "limping", "literate", "little", "lively", "lively", "living", "lonely", "long", "longing", "lopsided", "loud", "loutish", "lovely", "loving", "lucky", "ludicrous", "lumpy", "lush", "luxuriant", "lying", "lyrical",
//            "macabre", "macho", "maddening", "madly", "magenta", "magical", "magnificent", "majestic", "makeshift", "male", "massive", "married", "marvelous", "material", "materialistic", "mature", "measly", "medical", "mellow", "melodic", "melted", "merciful", "mere", "messy", "mighty", "military", "milky", "mindless", "miniature", "minor", "miscreant", "misty", "mixed", "moaning", "modern", "moldy", "momentous", "motionless", "mountainous", "muddled", "mundane", "mushy", "mute", "mysterious",
//            "naive", "nappy", "narrow", "nasty", "naughty", "nauseating", "neat", "nebulous", "necessary", "needless", "needy", "neighborly", "nervous", "nice", "nifty", "nimble", "nippy", "noiseless", "noisy", "nonchalant", "nondescript", "nonstop", "normal", "nostalgic", "nosy", "noxious", "numerous", "nutritious", "nutty",
//            "oafish", "obedient", "obeisant", "obese", "obnoxious", "obscene", "obsequious", "observant", "obsolete", "obtainable", "oceanic", "old", "old-fashioned", "omniscient", "one", "onerous", "open", "opposite", "optimal", "orange", "ordinary", "organic", "ossified", "outgoing", "outrageous", "outstanding", "oval", "overconfident", "overrated", "overt", "overwrought",
//            "pale", "paltry", "panicky", "panoramic", "parched", "parsimonious", "past", "pastoral", "pathetic", "peaceful", "penitent", "perfect", "periodic", "perpetual", "petite", "petite", "phobic", "physical", "picayune", "pink", "piquant", "placid", "plain", "plant", "plastic", "pleasant", "plucky", "pointless", "poised", "polite", "political", "poor", "possessive", "possible", "powerful", "precious", "premium", "present", "pretty", "previous", "pricey", "prickly", "private", "productive", "profuse", "protective", "proud", "psychedelic", "puffy", "pumped", "puny", "purple", "purring", "pushy", "puzzled", "puzzling",
//            "quaint", "quarrelsome", "questionable", "quick", "quiet", "quirky", "quixotic", "quizzical",
//            "rabid", "rainy", "rambunctious", "rampant", "rapid", "rare", "raspy", "ratty", "rebel", "receptive", "red", "reflective", "regular", "relieved", "remarkable", "reminiscent", "repulsive", "resolute", "resonant", "responsible", "rhetorical", "rich", "right", "righteous", "rightful", "rigid", "ripe", "ritzy", "roasted", "robust", "romantic", "rotten", "rough", "round", "royal", "ruddy", "rude", "rural", "rustic", "ruthless",
//            "sable", "sad", "safe", "salty", "same", "sassy", "satisfying", "savory", "scandalous", "scarce", "scared", "scary", "scattered", "scientific", "scintillating", "scrawny", "screeching", "second", "secret", "secretive", "sedate", "seemly", "selective", "selfish", "separate", "serious", "shaggy", "shaky", "sharp", "shiny", "shivering", "shocking", "short", "shrill", "shy", "silent", "silent", "silky", "silly", "simplistic", "sincere", "skillful", "skinny", "sleepy", "slippery", "sloppy", "slow", "small", "smart", "smiling", "smoggy", "smooth", "sneaky", "snobbish", "snotty", "soft", "soggy", "solid", "somber", "sophisticated", "sordid", "sour", "sparkling", "special", "spectacular", "spicy", "spiffy", "spiky", "spiritual", "spiteful", "splendid", "spooky", "spotless", "spotted", "spotty", "spurious", "squalid", "square", "staking", "stale", "statuesque", "steadfast", "steady", "steep", "sticky", "stiff", "stimulating", "stingy", "stormy", "straight", "strange", "striped", "strong", "stupendous", "sturdy", "substantial", "successful", "sulky", "super", "superb", "superficial", "supreme", "sweet", "sweltering", "swift",
//            "taboo", "tacit", "tacky", "talented", "tall", "tame", "tangible", "tangy", "tart", "tasteful", "tasteless", "tasty", "tawdry", "tearful", "tedious", "teeny", "tender", "tense", "tenuous", "terrific", "tested", "testy", "thankful", "therapeutic", "thinkable", "thirsty", "thoughtful", "thoughtless", "thundering", "tidy", "tiny", "tired", "tiresome", "toothsome", "torpid", "tough", "towering", "tranquil", "tricky", "trite", "truculent", "true", "truthful", "typical",
//            "ubiquitous", "unaccountable", "unadvised", "unarmed", "unbiased", "uncovered", "unequaled", "unhealthy", "uninterested", "unique", "unknown", "unruly", "unsightly", "unsuitable", "untidy", "unusual", "unwieldy", "uppity", "upset", "useful", "utopian",
//            "vacuous", "vagabond", "vague", "valuable", "vast", "vengeful", "venomous", "verdant", "versed", "victorious", "vigorous", "violent", "violet", "vivacious", "voiceless", "volatile", "voracious", "vulgar",
//            "wacky", "waggish", "waiting", "wakeful", "wandering", "wanting", "warlike", "warm", "wary", "watery", "weak", "wealthy", "weary", "well-groomed", "well-made", "well-off", "well-to-do", "whimsical", "whispering", "wide-eyed", "wiggly", "wild", "windy", "wiry", "wise", "wistful", "witty", "woebegone", "womanly", "wonderful", "wooden", "woozy", "worried"
//
//    };
//    /**
//     * https://github.com/hzlzh/Domain-Name-List/blob/master/Animal-words.txt
//     */
//    public static final String[] animalNames = new String[]{
//            "Aardvark",
//            "Akbash",
//            "Akita",
//            "Albatross",
//            "Alligator",
//            "Alpaca",
//            "Angelfish",
//            "Ant",
//            "Anteater",
//            "Antelope",
//            "Ape",
//            "Armadillo",
//            "Avocet",
//            "Axolotl",
//            "Baboon",
//            "Badger",
//            "Bandicoot",
//            "Barb",
//            "Barnacle",
//            "Barracuda",
//            "Bat",
//            "Beagle",
//            "Bear",
//            "Beaver",
//            "Bee",
//            "Beetle",
//            "Binturong",
//            "Bird",
//            "Birman",
//            "Bison",
//            "Bloodhound",
//            "Boar",
//            "Bobcat",
//            "Bongo",
//            "Bonobo",
//            "Booby",
//            "Budgerigar",
//            "Buffalo",
//            "Bulldog",
//            "Bullfrog",
//            "Butterfly",
//            "Caiman",
//            "Camel",
//            "Capybara",
//            "Caracal",
//            "Caribou",
//            "Cassowary",
//            "Cat",
//            "Caterpillar",
//            "Catfish",
//            "Centipede",
//            "Chameleon",
//            "Chamois",
//            "Cheetah",
//            "Chicken",
//            "Chihuahua",
//            "Chimpanzee",
//            "Chinchilla",
//            "Chinook",
//            "Chipmunk",
//            "Chough",
//            "Cichlid",
//            "Clam",
//            "Coati",
//            "Cobra",
//            "Cockroach",
//            "Cod",
//            "Collie",
//            "Coral",
//            "Cormorant",
//            "Cougar",
//            "Cow",
//            "Coyote",
//            "Crab",
//            "Crane",
//            "Crocodile",
//            "Crow",
//            "Curlew",
//            "Cuttlefish",
//            "Dachshund",
//            "Dalmatian",
//            "Deer",
//            "Dhole",
//            "Dingo",
//            "Dinosaur",
//            "Dodo",
//            "Dog",
//            "Dogfish",
//            "Dolphin",
//            "Donkey",
//            "Dormouse",
//            "Dotterel",
//            "Dove",
//            "Dragonfly",
//            "Drever",
//            "Duck",
//            "Dugong",
//            "Dunker",
//            "Dunlin",
//            "Eagle",
//            "Earwig",
//            "Echidna",
//            "Eel",
//            "Eland",
//            "Elephant",
//            "Elephant seal",
//            "Elk",
//            "Emu",
//            "Falcon",
//            "Ferret",
//            "Finch",
//            "Fish",
//            "Flamingo",
//            "Flounder",
//            "Fly",
//            "Fossa",
//            "Fox",
//            "Frigatebird",
//            "Frog",
//            "Galago",
//            "Gar",
//            "Gaur",
//            "Gazelle",
//            "Gecko",
//            "Gerbil",
//            "Gharial",
//            "Giant Panda",
//            "Gibbon",
//            "Giraffe",
//            "Gnat",
//            "Gnu",
//            "Goat",
//            "Goldfish",
//            "Goose",
//            "Gopher",
//            "Gorilla",
//            "Goshawk",
//            "Grasshopper",
//            "Greyhound",
//            "Grouse",
//            "Guanaco",
//            "Guinea fowl",
//            "Guinea pig",
//            "Gull",
//            "Guppy",
//            "Hamster",
//            "Hare",
//            "Harrier",
//            "Havanese",
//            "Hawk",
//            "Hedgehog",
//            "Heron",
//            "Herring",
//            "Hippopotamus",
//            "Hornet",
//            "Horse",
//            "Human",
//            "Hummingbird",
//            "Hyena",
//            "Ibis",
//            "Iguana",
//            "Impala",
//            "Indri",
//            "Insect",
//            "Jackal",
//            "Jaguar",
//            "Javanese",
//            "Jay",
//            "Jellyfish",
//            "Kakapo",
//            "Kangaroo",
//            "Kingfisher",
//            "Kiwi",
//            "Koala",
//            "Komodo dragon",
//            "Kouprey",
//            "Kudu",
//            "Labradoodle",
//            "Ladybird",
//            "Lapwing",
//            "Lark",
//            "Lemming",
//            "Lemur",
//            "Leopard",
//            "Liger",
//            "Lion",
//            "Lionfish",
//            "Lizard",
//            "Llama",
//            "Lobster",
//            "Locust",
//            "Loris",
//            "Louse",
//            "Lynx",
//            "Lyrebird",
//            "Macaw",
//            "Magpie",
//            "Mallard",
//            "Manatee",
//            "Mandrill",
//            "Markhor",
//            "Marten",
//            "Mastiff",
//            "Mayfly",
//            "Meerkat",
//            "Millipede",
//            "Mink",
//            "Mole",
//            "Molly",
//            "Mongoose",
//            "Mongrel",
//            "Monkey",
//            "Moorhen",
//            "Moose",
//            "Mosquito",
//            "Moth",
//            "Mouse",
//            "Mule",
//            "Narwhal",
//            "Neanderthal",
//            "Newfoundland",
//            "Newt",
//            "Nightingale",
//            "Numbat",
//            "Ocelot",
//            "Octopus",
//            "Okapi",
//            "Olm",
//            "Opossum",
//            "Orangutan",
//            "Oryx",
//            "Ostrich",
//            "Otter",
//            "Owl",
//            "Ox",
//            "Oyster",
//            "Pademelon",
//            "Panther",
//            "Parrot",
//            "Partridge",
//            "Peacock",
//            "Peafowl",
//            "Pekingese",
//            "Pelican",
//            "Penguin",
//            "Persian",
//            "Pheasant",
//            "Pig",
//            "Pigeon",
//            "Pika",
//            "Pike",
//            "Piranha",
//            "Platypus",
//            "Pony",
//            "Poodle",
//            "Porcupine",
//            "Porpoise",
//            "Possum",
//            "Prairie Dog",
//            "Prawn",
//            "Puffin",
//            "Pug",
//            "Puma",
//            "Quail",
//            "Quelea",
//            "Quetzal",
//            "Quokka",
//            "Quoll",
//            "Rabbit",
//            "Raccoon",
//            "Ragdoll",
//            "Rail",
//            "Ram",
//            "Rat",
//            "Rattlesnake",
//            "Raven",
//            "Red deer",
//            "Red panda",
//            "Reindeer",
//            "Rhinoceros",
//            "Robin",
//            "Rook",
//            "Rottweiler",
//            "Ruff",
//            "Salamander",
//            "Salmon",
//            "Sand Dollar",
//            "Sandpiper",
//            "Saola",
//            "Sardine",
//            "Scorpion",
//            "Sea lion",
//            "Sea Urchin",
//            "Seahorse",
//            "Seal",
//            "Serval",
//            "Shark",
//            "Sheep",
//            "Shrew",
//            "Shrimp",
//            "Siamese",
//            "Siberian",
//            "Skunk",
//            "Sloth",
//            "Snail",
//            "Snake",
//            "Snowshoe",
//            "Somali",
//            "Sparrow",
//            "Spider",
//            "Sponge",
//            "Squid",
//            "Squirrel",
//            "Starfish",
//            "Starling",
//            "Stingray",
//            "Stinkbug",
//            "Stoat",
//            "Stork",
//            "Swallow",
//            "Swan",
//            "Tapir",
//            "Tarsier",
//            "Termite",
//            "Tetra",
//            "Tiffany",
//            "Tiger",
//            "Toad",
//            "Tortoise",
//            "Toucan",
//            "Tropicbird",
//            "Trout",
//            "Tuatara",
//            "Turkey",
//            "Turtle",
//            "Uakari",
//            "Uguisu",
//            "Umbrellabird",
//            "Vicuña",
//            "Viper",
//            "Vulture",
//            "Wallaby",
//            "Walrus",
//            "Warthog",
//            "Wasp",
//            "Water buffalo",
//            "Weasel",
//            "Whale",
//            "Whippet",
//            "Wildebeest",
//            "Wolf",
//            "Wolverine",
//            "Wombat",
//            "Woodcock",
//            "Woodlouse",
//            "Woodpecker",
//            "Worm",
//            "Wrasse",
//            "Wren",
//            "Yak",
//            "Zebra",
//            "Zebu",
//            "Zonkey",
//            "Zorse"};
}
