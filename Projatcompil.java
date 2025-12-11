

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

    // ANALYSE LEXICALE
    public class lexical {
        public static boolean lexemes(String c, ArrayList<Token> liste) {
            int line = 1;
            Token l;
            c += "#";
            int i = 0, d = 0, j, n = c.length();
            String mot;

            while (c.charAt(i) != '#') {
                if (c.charAt(i) == '\n') {
                    line++;
                }

                if (c.charAt(i) == '"') {
                    mot = "";
                    for (j = d; j < i; j++) {
                        mot += c.charAt(j);
                    }
                    if (!mot.trim().equals("")) {
                        String type = getType(mot);
                        l = new Token(type, mot, line);
                        liste.add(l);
                        System.out.println(mot + "(" + type + ")");
                    }

                    d = i;
                    i++;
                    mot = "\"";

                    while (i < n && c.charAt(i) != '"') {
                        mot += c.charAt(i);
                        i++;
                    }

                    if (i < n && c.charAt(i) == '"') {
                        mot += "\"";
                    }
                    if (getType(mot).equals("0")) {
                        System.out.println("Erreur lexicale : chaine non fermée " + mot + " à la ligne " + line);
                        return false;
                    }
                    l = new Token(getType(mot), mot, line);
                    liste.add(l);

                    i++;
                    d = i;
                } else {
                    if (!(Character.isLetterOrDigit(c.charAt(i)) || c.charAt(i) == '_')) {
                        mot = "";

                        if (Character.isWhitespace(c.charAt(i))) {
                            for (j = d; j < i; j++) {
                                mot += c.charAt(j);
                            }
                            if (!mot.trim().equals("")) {
                                if (getType(mot).equals("0")) {
                                    System.out.println("Erreur lexicale : mot non reconnu -> " + mot + " dans la ligne " + line);
                                    return false;
                                }
                                l = new Token(getType(mot), mot, line);
                                liste.add(l);
                            }
                            d = i + 1;
                        } else {
                            for (j = d; j < i; j++) {
                                mot += c.charAt(j);
                            }
                            if (!mot.trim().equals("")) {
                                if (getType(mot).equals("0")) {
                                    System.out.println("Erreur lexicale : mot non reconnu -> " + mot + " dans la ligne " + line);
                                    return false;
                                }
                                l = new Token(getType(mot), mot, line);
                                liste.add(l);
                            }

                            mot = "";
                            // Gestion des opérateurs doubles et comparateurs
                            if (i + 1 < n) {
                                String deuxCar = "" + c.charAt(i) + c.charAt(i + 1);
                                if (deuxCar.equals("==") || deuxCar.equals("<=") || deuxCar.equals(">=") ||
                                    deuxCar.equals("++") || deuxCar.equals("--") ||
                                    deuxCar.equals("+=") || deuxCar.equals("-=")) {
                                    mot = deuxCar;
                                    i++;
                                } else {
                                    mot = "" + c.charAt(i);
                                }
                            } else {
                                mot = "" + c.charAt(i);
                            }

                            if (!mot.trim().equals("")) {
                                if (getType(mot).equals("0")) {
                                    System.out.println("Erreur lexicale : mot non reconnu -> " + mot + " dans la ligne " + line);
                                    return false;
                                }
                                l = new Token(getType(mot), mot, line);
                                liste.add(l);
                            }
                            d = i + 1;
                        }
                    }
                    i++;
                }
            }
            return true;
        }

        public static String getType(String s) {
            int i = 0;
            if (s.equals("tidjet") || s.equals("mailiz") || s.equals("try") || s.equals("catch") || s.equals("break") || s.equals("this") ||
                s.equals("new") || s.equals("let") || s.equals("var") || s.equals("const") || s.equals("class") || s.equals("case") ||
                s.equals("if") || s.equals("while") || s.equals("else") || s.equals("do") || s.equals("for") || s.equals("foreach") ||
                s.equals("function") || s.equals("return") || s.equals("switch") || s.equals("default") || s.equals("continue") ||
                s.equals("throw") || s.equals("finally") || s.equals("true") || s.equals("false") || s.equals("null") ||
                s.equals("undefined") || s.equals("import") || s.equals("export") || s.equals("from") || s.equals("async") ||
                s.equals("await") || s.equals("in") || s.equals("of") || s.equals("extends") || s.equals("super") ||
                s.equals("alert")) {
                return "MotCle";
            }

            if (s.length() >= 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                return "string";
            }
            if (Character.isDigit(s.charAt(i))) {
                s += '#';
                i++;
                while (Character.isDigit(s.charAt(i))) {
                    i++;
                }
                if (s.charAt(i) == '#' && i + 1 == s.length()) {
                    return "nombre";
                } else {
                    return "0";
                }
            }
            if (Character.isLetter(s.charAt(i))) {
                s += '#';
                i++;
                while (Character.isDigit(s.charAt(i)) || Character.isLetter(s.charAt(i)) || s.charAt(i) == '_') {
                    i++;
                }
                if (s.charAt(i) == '#' && i + 1 == s.length()) {
                    return "id";
                } else {
                    return "0";
                }
            }

            if (s.equals("<") || s.equals(">") || s.equals("==") || s.equals("<=") || s.equals(">=")) {
                return "comparateur";
            }
            if (s.equals("++") || s.equals("--")) {
                return "incrementation";
            }
            if (s.equals(",") || s.equals(")") || s.equals("(") || s.equals(";") || s.equals(".") || s.equals("}") || s.equals("{")) {
                return "separateur";
            }
            if (s.equals("+") || s.equals("-") || s.equals("/") || s.equals("*")) {
                return "operateur";
            }
            if (s.equals("=") || s.equals("+=") || s.equals("-=")) {
                return "affectation";
            }
            return "0";
        }
    }

    // ANALYSE SYNTAXIQUE
    public static class syntaxic {
        ArrayList<Token> c;
        static int i = 0;
        static boolean test = true;

        syntaxic(ArrayList<Token> c) {
            this.c = c;
        }

        void Z() {
            c.add(new Token("#", "#", 0));
            S();
            if (test && c.get(i).type.equals("#") && i + 1 == c.size()) {
                System.out.println("votre code est correct");
            } else {
                System.out.println("votre code n'est pas correct");
            }
        }

        void S() {
            if (!test) return;
            if (c.get(i).value.equals("function")) {
                i++;
                if (c.get(i).type.equals("id")) {
                    i++;
                } else {
                    test = false;
                    System.out.println("identificateur manque dans la ligne " + c.get(i).ligne);
                    return;
                }
                if (c.get(i).value.equals("(")) {
                    i++;
                    entre();
                    if (!test) return;
                    if (c.get(i).value.equals(")")) {
                        i++;
                        block();
                        if (!test) return;
                    } else {
                        test = false;
                        System.out.println("ferme la parentese dans la ligne" + c.get(i).ligne);
                        return;
                    }
                } else {
                    test = false;
                    System.out.println("la fonction doit avoir des entré , ligne " + c.get(i).ligne);
                    return;
                }
            } else {
                instructions();
                if (!test) return;
            }
        }

        void entre() {
            if (!test) return;
            if (!c.get(i).value.equals(")")) {
                valeur();
                if (!test) return;
                e();
                if (!test) return;
            }
        }

        void block() {
            if (c.get(i).value.equals("{")) {
                i++;
                instructions();
                if (!test) return;
                if (c.get(i).value.equals("}")) {
                    i++;
                } else {
                    test = false;
                    System.out.println(" } is not used , ligne  " + c.get(i).ligne);
                    return;
                }
            } else {
                test = false;
                System.out.println("expected { en  ligne " + c.get(i).ligne);
                return;
            }
        }

        void instructions() {
            if (!test) return;

            // if/else
            if (c.get(i).value.equals("if")) {
                i++;
                if (c.get(i).value.equals("(")) {
                    i++;
                    valeur();
                    if (!test) return;
                    if (c.get(i).value.equals(")")) {
                        i++;
                        block();
                        if (!test) return;

                        if (c.get(i).value.equals("else")) {
                            i++;
                            block();
                            if (!test) return;
                        }

                        instructions();
                        if (!test) return;
                    } else {
                        test = false;
                        System.out.println("Parenthèse fermante ) attendue à la ligne " + c.get(i).ligne);
                        return;
                    }
                } else {
                    test = false;
                    System.out.println("Parenthèse ouvrante ( attendue après if à la ligne " + c.get(i).ligne);
                    return;
                }
            } else if (!c.get(i).value.equals("}") && !c.get(i).value.equals("#")) {
                instruction();
                if (!test) return;

                if (c.get(i).value.equals(";")) {
                    i++;
                } else {
                    test = false;
                    System.out.println("Point-virgule ; attendu à la ligne " + c.get(i).ligne);
                    return;
                }
                instructions();
                if (!test) return;
            }
        }

        void valeur() {
            if (!test) return;
            if (c.get(i).type.equals("nombre") || c.get(i).type.equals("id") || c.get(i).type.equals("string")) {
                i++;
                V();
                if (!test) return;
            } else if (c.get(i).value.equals("(")) {
                i++;
                valeur();
                if (!test) return;

                if (c.get(i).value.equals(")")) {
                    i++;
                    V();
                    if (!test) return;
                } else {
                    test = false;
                    System.out.println("ferme la parentese en ligne " + c.get(i).ligne);
                    return;
                }
            } else {
                test = false;
                System.out.println("erreur en ligne " + c.get(i).ligne);
                return;
            }
        }

        void V() {
            if (!test) return;
            if (c.get(i).type.equals("operateur") || c.get(i).type.equals("comparateur")) {
                i++;
                valeur();
                if (!test) return;
            }
        }

        void e() {
            if (!test) return;
            if (c.get(i).value.equals(",")) {
                i++;
                valeur();
                if (!test) return;
                e();
                if (!test) return;
            }
        }

        void declaration() {
            if (!test) return;
            if (c.get(i).value.equals("let") || c.get(i).value.equals("const") || c.get(i).value.equals("var")) {
                i++;
                if (c.get(i).type.equals("id")) {
                    i++;
                    D();
                    if (!test) return;
                } else {
                    System.out.println("id is expected at line" + c.get(i).ligne);
                    return;
                }
            } else {
                test = false;
                if (c.get(i).type.equals("MotCle")) {
                    System.out.println("instruction non traité par ce mini compilateur, ligne " + c.get(i).ligne);
                    return;
                }
                System.out.println("instruction non reconnue");
                return;
            }
        }

        void D() {
            if (!test) return;
            if (c.get(i).value.equals("=")) {
                i++;
                valeur();
                if (!test) return;
            }
        }

        void A() {
            if (!test) return;
            if (c.get(i).value.equals(".")) {
                i++;
                if (c.get(i).type.equals("id")) {
                    i++;
                    A();
                    if (!test) return;
                } else {
                    test = false;
                    System.out.println("fonction non reconnue , ligne" + c.get(i).ligne);
                    return;
                }
            } else if (c.get(i).value.equals("(")) {
                i++;
                entre();
                if (!test) return;
                if (c.get(i).value.equals(")")) {
                    i++;
                } else {
                    test = false;
                    System.out.println("( is expected at line" + c.get(i).ligne);
                    return;
                }
            } else {
                test = false;
                System.out.println("erreur dans la ligne" + c.get(i).ligne);
                return;
            }
        }

        void instruction() {
            if (!test) return;
            if (c.get(i).type.equals("id")) {
                i++;
                CalcApl();
                if (!test) return;
            } else if (c.get(i).value.equals("alert")) {
                i++;
                if (c.get(i).value.equals("(")) {
                    i++;
                    valeur();
                    if (!test) return;
                    if (c.get(i).value.equals(")")) {
                        i++;
                    } else {
                        test = false;
                        System.out.println("parentese non fermé a la ligne " + c.get(i).ligne);
                    }
                } else {
                    test = false;
                    System.out.println("il manque une ( a la ligne " + c.get(i).ligne);
                }
            } else {
                declaration();
                if (!test) return;
            }
        }

        void CalcApl() {
            if (c.get(i).type.equals("incrementation")) {
                i++;
            } else if (c.get(i).type.equals("affectation")) {
                i++;
                valeur();
                if (!test) return;
            } else {
                A();
                if (!test) return;
            }
        }
    }

    public static void main(String args[]) {
        try {
            ArrayList<Token> liste = new ArrayList<Token>();
            String code = new String(Files.readAllBytes(Paths.get(
                    "C:\\Users\\PC\\Documents\\NetBeansProjects\\projatcompilation\\src\\projatcompilation\\t.js")),
                    StandardCharsets.UTF_8);
            boolean k;
            k = lexical.lexemes(code, liste);

            if (k) {
                System.out.println(" LE RESULTAT DE L'ANALYSE LEXICALE  :");
                for (int i = 0; i < liste.size(); i++) {
                    liste.get(i).afficher();
                }
                System.out.println(" LE RESULTAT DE L'ANALYSE SYNTAXIQUE : ");
                syntaxic s = new syntaxic(liste);
                s.Z();
            }

        } catch (IOException e) {
            System.out.println("Erreur lecture fichier : " + e.getMessage());
        }
    }
}
