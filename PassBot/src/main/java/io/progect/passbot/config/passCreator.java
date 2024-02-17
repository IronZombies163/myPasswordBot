package io.progect.passbot.config;

import org.springframework.stereotype.Component;

import java.util.Random;
@Component

public class passCreator {
    private void shakeArr(String [] arr){
        Random rnd = new Random();
        for(int i = 0; i < arr.length; i++) {
            int index = rnd.nextInt(i + 1);
            String a = arr[index];
            arr[index] = arr[i];
            arr[i] = a;
        }
    }
    public String creatPassword(){
        String [] optionsPassPart= {"word", "emoji", "numbers", "letters", "symbols"};
        Random random = new Random();
        StringBuilder stringBuilder= new StringBuilder();
        shakeArr(optionsPassPart);
        for(String el: optionsPassPart){
            switch (el) {
                case "word" -> {
                    String[] words = {"Will", " Milk", "Cheese", "Chill", "Epica", "КряКря", "Jelly",
                            "Beolact", " Flogurt", "Danone", "Dinoco", "NeMilk", "Chydo", " Teoss ", " Danissim@", "Actimel",
                            "Oikos", " NatureDino", "Pelican", " Duck", "Doccer", "Trundle", "Doctor", "Clever", " HardCode", "Name", "Drago", "Ziplab", "Lyngvae",
                            "Kabvema", "Lianya", " meoowww", "GothBoy", " Ghost", "Phantom", "Aron", "handle", "hand", "Hair", "Notebook",
                            "Kniga", "Pencil", "Bathrobe", "Table", "Chair", "Tea", "Coffee", "Music", "Drawing", "River", "Character",
                            "Jacket", "Animal", "Grass", "Nails", "Board", "Chalk", "History", "Mathematics", "ComputerScience", "Physics",
                            "Tournament", "Prize", "Pizza", "Cartoon", "Movie", "TV", "Circle", "Square", "Shape",
                            "Spring", "Summer", "Winter", "Autumn", "Rain", "Snow", "Wind", "Water", "Fire", "Stone", "Earth", "Planet", "Orange",
                            "Aloha", "Ohana", "Santix", "Santos", " Sendy", "Wendy", "Sinister ", "Dexter", "Cake", "Kyn", "Sunset", "Sansei", " Born",
                            "T-rex", "Knife", "Aurora", "сияние", "Bumblebee", "bee", "Galaxy", "Meadow", "Sunshine", "Sweet", "heart",
                            "Oblivion", "Success", "Parma", "Hochland", "Milcom", "Agusha", "Жарупели", "TostLand", "Cotlin", "Ruby", "Nintendo", "Ubisoft",
                            "OnePiece", "Asce", "Cry", "Lyffi", "Konami", "Bandai", "Blizzard", "Project", "Crasher", "Execute", "Linux",
                            "Riot", "Silver", "DogCoin", "Kotlin", "Angelar", "Axe", "Hecarim", "Yasuo", "Stitch", "Pixar", "Reddit",
                            "Honey", "Wolf", "Shark", "Pengvin", "Snake", "Milka", "Dual", "Ferb", "Kusko", "Pearl", "Breeze", "Isebox",
                            "Bind", "Haven", "Split", "Ascent", "Sage", "Owen", "Omen", "Neon", "Lumous", "Serene", "Whimsical",
                            "Melli", "Effective", "Benevo", "lent", "Halcyon", "Susurrus", "Mario", "ChatGPT", "Jett", "Akali", "Yasuo",
                            "Magic", "Mickey", "Castle", "Pixar", "Dreams", "HappyPass", "Imagine", "Wonder", "Fantasy", "Toon", "Fantasia",
                            "Wish", "Fairy", "Epcot", "SpongeBob", "Slime", "Nicktoon", "Rugrats", "Bumblebee", "Jellyfish", "Noodle", "Penguin", "Giggly",
                            "Silly", "Banana", "Peel", "Taco", "Waffle", "Doodle", "Funky", "Cheese", "Chuck", "Muffin", "Penguin", "Pajamas", "Snicker", "Snail",
                            "Zany", "Stripe", "JollyBean", "Quirky", "Quokka", "Merry", "Marshmallow", "Sasquatch", "Cheerful",
                            "Sunny", "Smile", "Dizzy", "Dolphin", "Lively", "LlamaLeap", "Wacky", "Watermelon", "Jam", "Playful", "Zippy", "Zigzag", "Gazelle",
                            "Chipper", "Lion", "Mango", "Manga", "Sassy", "Slide", "Dandy", "Dragon", "Froggi", "Frog",
                            "Pikachu", "Charizard", "Squirtle", "Snorlax", "Mewtwo", "Eevee", "Machamp", "Gyarados", "Pluto", "Elsa",
                            "Mirida", "Goofy", "Aladdin", "Simba", "Nemo", "Dory", "Moana", "BMW", "Timon", "Pumba", "Woody", "Buzz-Lightyear",
                            "Sven", "Lady", "Evil", "Brim", "Ursula", "Mushu", "Aid", "Zeus", "Dragon", "Flame", "Silver", "Golden", "Gold",
                            "Ocean", "Breeze", "Echo", "Mystic", "River", "Shadow", "Lo-Fi", "Shoot", "Fukurou", "Bunny", "Suzume", "Ciao",
                            "Celoso", "Umbrella", "Lissabon", "Marriot", "Bingo", "Katana", "Idk.", "Forever", "Sayonara", "Espada", "Ariva", "Lumos", "Nox"
                    };
                    int index = random.nextInt(words.length);
                    stringBuilder.append(words[index].trim());
                }

                case "emoji" -> {
                    String[] emoji = {"=)", ":)", "(・・ )?", "(о_О)", "(^○^)","(o.O)", "@_@", "(2_2)", "(>-<)", "(@_@)",
                            "(>0<)", "(^^)", ":^)", "(*_*)", "(*О*)", "(>_<)", "(^-^)", "(x . x)", "(・・ )?", ":D", "(#_#)",
                            "О_О", "(v_v)", "(<_<) ", "(*^_^*)", "(o_o)", "(0_0)", "(%_%)", "(.^.)", "('^')", "(u_u)", "(>x<!)", "(9_9)", "(=__=)",
                            "(-_-v)", "(-_-)", "(^*^)", "(T^T)","(T_T)", "(._.)", "^^", "(TT)", "(T-T)", "(^~^)", "()_()", "('~')", "(>~<)", ">~<", "(.~.)"};
                    int index2 = random.nextInt(emoji.length);
                    stringBuilder.append(emoji[index2]);
                }
                case "numbers" -> {
                    int nums = (int) (Math.random() * 100);
                    stringBuilder.append(nums);
                }
                case "symbols" -> {
                    char[] symbol = {'!', '?', '@', '-',};
                    int i = random.nextInt(symbol.length);
                    stringBuilder.append(symbol[i]);
                }
            }
        }
        return String.valueOf(stringBuilder);
    }


}
