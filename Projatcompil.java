package projatcompil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Projatcompil {

    // Token
    public static class Token {
        String type;
        String value;
        int ligne;

        Token(String type, String value, int ligne) {
            this.type = type;
            this.value = value;
            this.ligne = ligne;
        }

        public void afficher() {
            System.out.println(value + "  => " + type + " line (" + ligne + ")");
        }
    }

    // ================================
    // ANALYSE LEXICALE
    // ================================
    public static class lexical {

        public static ArrayList<Token> lexemes(String c) {
            int ligne = 1;
            Token l;
            c += "#";
            int i = 0, d = 0, j;
            int n = c.length();
            String mot;
            ArrayList<Token> liste = new ArrayList<>();

            while ((i < n) && (c.charAt(i) != '#')) {
                if (c.charAt(i) == '\n') {
                    ligne++;
                }

                if (c.charAt(i) == '"') {
                    mot = "";
                    for (j = d; j < i; j++) mot += c.charAt(j);
                    if (!mot.trim().equals("")) {
                        l = new Token(getType(mot), mot, ligne);
                        liste.add(l);
                    }

                    d = i;
                    i++;
                    mot = "\"";
                    while (i < n && c.charAt(i) != '"') {
                        mot += c.charAt(i);
                        i++;
                    }
                    if (i < n && c.charAt(i) == '"') mot += "\"";

                    if (getType(mot).equals("0")) {
                        System.out.println("Erreur lexicale : chaine non fermée " + mot + " ligne " + ligne);
                        return liste;
                    }
                    l = new Token(getType(mot), mot, ligne);
                    liste.add(l);
                    i++;
                    d = i;
                } else {
                    if (!(Character.isLetterOrDigit(c.charAt(i)) || c.charAt(i) == '_')) {
                        mot = "";

                        if (Character.isWhitespace(c.charAt(i))) {
                            for (j = d; j < i; j++) mot += c.charAt(j);
                            if (!mot.trim().equals("")) {
                                if (getType(mot).equals("0")) {
                                    System.out.println("Erreur lexicale : mot non reconnu -> " + mot + " ligne " + ligne);
                                    return liste;
                                }
                                l = new Token(getType(mot), mot, ligne);
                                liste.add(l);
                            }
                            d = i + 1;
                        } else {
                            for (j = d; j < i; j++) mot += c.charAt(j);
                            if (!mot.trim().equals("")) {
                                if (getType(mot).equals("0")) {
                                    System.out.println("Erreur lexicale : mot non reconnu -> " + mot + " ligne " + ligne);
                                    return liste;
                                }
                                l = new Token(getType(mot), mot, ligne);
                                liste.add(l);
                            }

                            mot = "";
                            if ((c.charAt(i) == '+' || c.charAt(i) == '-') &&
                                (c.charAt(i + 1) == c.charAt(i) || c.charAt(i + 1) == '=')) {
                                mot += c.charAt(i);
                                mot += c.charAt(i + 1);
                                i++;
                            } else {
                                mot += c.charAt(i);
                            }

                            if (!mot.trim().equals("")) {
                                if (getType(mot).equals("0")) {
                                    System.out.println("Erreur lexicale : mot non reconnu -> " + mot + " ligne " + ligne);
                                    Projatcompil.k = 0;
                                    return liste;
                                }
                                l = new Token(getType(mot), mot, ligne);
                                liste.add(l);
                            }
                            d = i + 1;
                        }
                    }
                    i++;
                }
            }
            return liste;
        }

        public static String getType(String s) {
            int i = 0;

            // Mots clés
            if (s.equals("if") || s.equals("else"))
                return "MOTCLE";

            // Chaîne
            if (s.length() >= 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"')
                return "STRING";

            // Nombre
            if (Character.isDigit(s.charAt(i))) {
                s += '#';
                i++;
                while (Character.isDigit(s.charAt(i)))
                    i++;
                if (s.charAt(i) == '#' && i + 1 == s.length())
                    return "NOMBRE";
                return "0";
            }

            // Identifiant
            if (Character.isLetter(s.charAt(i))) {
                s += '#';
                i++;
                while (Character.isDigit(s.charAt(i)) || Character.isLetter(s.charAt(i)) || s.charAt(i) == '_')
                    i++;
                if (s.charAt(i) == '#' && i + 1 == s.length())
                    return "ID";
                return "0";
            }

            // Comparateurs
            if (s.equals("<") || s.equals(">") || s.equals("==") || s.equals("<=") || s.equals(">="))
                return "COMPARATEUR";

            // Parenthèses - accolades
            if (s.equals("(")) return "PAR_OUV";
            if (s.equals(")")) return "PAR_FER";
            if (s.equals("{")) return "ACC_OUV";
            if (s.equals("}")) return "ACC_FER";

            // Opérateurs
            if (s.equals("++") || s.equals("--"))
                return "INCREMENT";
            if (s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/"))
                return "OPERATEUR";
            if (s.equals("="))
                return "AFFECT";
            if (s.equals(";"))
                return "SEMI";

            return "0";
        }
    }

    // ================================
    // ANALYSE SYNTAXIQUE
    // ================================
    public static class syntaxic {
        ArrayList<Token> c;
        static int i = 0;
        static boolean test = true;
        Token erreurToken = null;

        syntaxic(ArrayList<Token> c) { this.c = c; }

        void Z() {
            c.add(new Token("#", "#", 0));
            S();
            if (test && c.get(i).value.equals("#")) {
                System.out.println("mot accepté");
            } else {
                System.out.println("mot non accepté ligne " + (erreurToken != null ? erreurToken.ligne : "?"));
            }
        }

        void S() {
            instructions();
        }

        // ================================
        // INSTRUCTIONS
        // ================================
        void instructions() {
            while (test && !c.get(i).value.equals("}") && !c.get(i).value.equals("#")) {

                if (c.get(i).value.equals("if")) {
                    ifInst();
                } else {
                    instruction();
                    if (!c.get(i).value.equals(";")) {
                        erreur("';' attendu");
                        return;
                    }
                    i++; // Consomme ;
                }
            }
        }

        // ================================
        // IF / ELSE IF / ELSE
        // ================================
        void ifInst() {
            i++; // if

            if (!c.get(i).value.equals("(")) {
                erreur("'(' attendu après if");
                return;
            }
            i++;

            valeur();

            if (!c.get(i).value.equals(")")) {
                erreur("')' attendu");
                return;
            }
            i++;

            block();

            // -------- ELSE / ELSE IF --------
            while (i < c.size() && c.get(i).value.equals("else")) {
                i++;

                // ELSE IF
                if (c.get(i).value.equals("if")) {
                    i++;

                    if (!c.get(i).value.equals("(")) {
                        erreur("'(' attendu après else if");
                        return;
                    }
                    i++;

                    valeur();

                    if (!c.get(i).value.equals(")")) {
                        erreur("')' attendu");
                        return;
                    }
                    i++;

                    block();
                }
                // ELSE
                else if (c.get(i).value.equals("{")) {
                    block();
                }
                else {
                    erreur("Bloc { attendu après else");
                    return;
                }
            }
        }

        // ================================
        // BLOCK { ... }
        // ================================
        void block() {
            if (!c.get(i).value.equals("{")) {
                erreur("'{' attendu");
                return;
            }
            i++;

            instructions();

            if (!c.get(i).value.equals("}")) {
                erreur("'}' attendu");
                return;
            }
            i++;
        }

        // ================================
        // INSTRUCTION
        // ================================
        void instruction() {
            if (c.get(i).type.equals("ID")) {
                i++;
                if (c.get(i).type.equals("AFFECT")) {
                    i++;
                    valeur();
                }
                else if (c.get(i).type.equals("INCREMENT")) {
                    i++;
                }
                else {
                    erreur("Affectation ou incrémentation attendue");
                }
            }
            else {
                erreur("Instruction inconnue");
            }
        }

        // ================================
        // VALEUR + opérations
        // ================================
        void valeur() {
            if (c.get(i).type.equals("NOMBRE") || c.get(i).type.equals("ID")) {
                i++;
                V();
            } else {
                erreur("Valeur incorrecte");
            }
        }

        void V() {
            while (c.get(i).type.equals("OPERATEUR") || c.get(i).type.equals("COMPARATEUR")) {
                i++;
                valeur();
            }
        }

        void erreur(String msg) {
            test = false;
            erreurToken = c.get(i);
            System.out.println(msg + " ligne " + c.get(i).ligne);
        }
    }


    public static int k = 1;

    public static void main(String args[]) {
        try {
            String code = new String(Files.readAllBytes(Paths.get(
                    "C:\\Users\\PC\\Documents\\NetBeansProjects\\projatcompil\\src\\test.js")),
                    StandardCharsets.UTF_8);

            ArrayList<Token> liste = lexical.lexemes(code);

            if (k == 1) {
                for (Token t : liste) t.afficher();
            }

            syntaxic syn = new syntaxic(liste);
            syn.Z();

        } catch (IOException e) {
            System.out.println("Erreur fichier : " + e.getMessage());
        }
    }
}
