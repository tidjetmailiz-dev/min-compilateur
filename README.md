Tabledesmatières
1 Introduction 2
1.1 ContexteduProjet . . . . . . . . . . . . . . . . . . . . . . . . . . . . 2
1.2 Objectifs . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 2
1.3 Outils . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 2
2 AnalyseLexicale 2
2.1 Définition . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 2
2.2 Structured’unToken . . . . . . . . . . . . . . . . . . . . . . . . . . . 3
2.3 ÉlémentsReconnaissables . . . . . . . . . . . . . . . . . . . . . . . . 3
3 AnalyseSyntaxique 3
3.1 Définition . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 3
3.2 GrammaireduProjet . . . . . . . . . . . . . . . . . . . . . . . . . . . 3
4 ExempledeProgrammeAccepté 4
5 Conclusion 4
1
1 Introduction
1.1 Contexte du Projet
Dans le cadre du module de compilation, nous avons développé un mini-compilateur
capable d’analyser un sous-ensemble d’un langage inspiré de JavaScript. Ce compi
lateur comporte deux parties principales :
— Analyse lexicale : transformation du texte en tokens
— Analyse syntaxique : vérification de la conformité à une grammaire
Le projet est entièrement développé en Java.
1.2 Objectifs
Les objectifs principaux sont :
— détecter les unités lexicales : identifiants, mots-clés, chaînes, opérateurs...
— vérifier la syntaxe selon une grammaire récursive
— prendre en charge fonctions, blocs, conditions, déclarations, opérations, etc.
— gérer les erreurs lexicales et syntaxiques
1.3 Outils
— Langage : Java
— Analyse lexicale : automate + matrice de transitions
— Analyse syntaxique : descente récursive
— IDE : NetBeans / IntelliJ / Eclipse
2 Analyse Lexicale
2.1 Définition
L’analyse lexicale lit le code source caractère par caractère et le découpe en
tokens. Chaque token contient :
— un type : ID, NOMBRE, OPERATEUR...
— une valeur
— la ligne
Exemple :
let x = 7;
x++;
Tokens produits :
— let → MOT_CLE
— x→ID
— =→AFFECTATION
— 7→NOMBRE
— ;→SEMI
— ++→INCREMENTATION
2
2.2 Structure d’un Token
public class Token {
String type;
String value;
int line;
public Token(String type, String value, int line) {
this.type = type;
this.value = value;
this.line = line;
}
}
2.3 Éléments Reconnaissables
— Mots-clés : function, let, var, if, else...
— Opérateurs : +,-, *, /, ==, >=, , ||, ++...
— Séparateurs : () {} [] , ;
— Identificateurs : commencent par lettre ou _
— Nombres : entiers, décimaux, hexadécimaux, binaires
— Chaînes : "texte", ’texte’
3 Analyse Syntaxique
3.1 Définition
L’analyse syntaxique vérifie que la suite de tokens forme un programme valide
selon la grammaire. Chaque méthode du parseur correspond à une règle.
3.2 Grammaire du Projet
<programme> ::= <instruction_principale> "#"

<instruction_principale> ::= "function" id "(" <params> ")" <bloc>
                           | <instructions>

<bloc> ::= "{" <instructions> "}"

<instructions> ::= <instruction> ";" <instructions>
                 | <if_else> <instructions>
                 | ε

<if_else> ::= "if" "(" <expression> ")" <bloc> [ "else" <bloc> ]

<instruction> ::= id <suite_id>
                | "alert" "(" <expression> ")"
                | <declaration>

<declaration> ::= ("let" | "const" | "var") id <affectation_opt>

<affectation_opt> ::= "=" <expression>
                    | ε

<suite_id> ::= <incrementation>
             | <affectation> <expression>
             | <appel>

<appel> ::= "." id <appel>
          | "(" <params> ")"
          | ε

<params> ::= <expression> <suite_params>
           | ε

<suite_params> ::= "," <expression> <suite_params>
                 | ε

<expression> ::= id <suite_expr>
               | nombre <suite_expr>
               | string <suite_expr>
               | "(" <expression> ")" <suite_expr>

<suite_expr> ::= ( <operateur> | <comparateur> ) <expression>
                 | ε

4 Exemple de Programme Accepté
function calc(a, b) {
let x = a + b;
x++;
if (x > 10) {
y = "grand";
} else {
y = "petit";
}
}
5 Conclusion
Ce projet nous a permis de :
— maîtriser l’analyse lexicale
— construire un parseur récursif
— comprendre la structure d’un compilateur
— gérer les erreurs syntaxiques
— implémenter un mini-langage proche de JavaScript
Le compilateur pourra être étendu dans le futur (boucles, retours de fonction,
analyse sémantique...).
