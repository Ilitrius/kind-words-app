# R8/ProGuard-Regeln der App

# ML Kit (Übersetzung): Die Komponenten-Registrare werden nur per Name aus dem Manifest geladen und würden sonst
# von R8 entfernt (Folge: NullPointerException in RemoteModelManager.getInstance() im Release-Build).
-keep class * implements com.google.firebase.components.ComponentRegistrar { *; }
-keep class com.google.mlkit.nl.translate.NaturalLanguageTranslateRegistrar { *; }
-keep class com.google.mlkit.common.internal.CommonComponentRegistrar { *; }

# WorkManager (kommt mit Glance für das Widget): InputMerger werden per Reflection über den Konstruktor ohne Argumente erzeugt.
# Ohne diese Regel entfernt R8 ihn ("Could not create Input Merger ... no zero argument constructor"); das Widget bleibt dann im Release-Build im Ladezustand.
-keep class * extends androidx.work.InputMerger { <init>(); }
-keep class * extends androidx.work.ListenableWorker { <init>(android.content.Context, androidx.work.WorkerParameters); }

# Glance (Widget): ActionCallback-Klassen werden per Reflection über den Konstruktor ohne Argumente erzeugt ("Neu"-Knopf).
-keep class * implements androidx.glance.appwidget.action.ActionCallback { <init>(); }
