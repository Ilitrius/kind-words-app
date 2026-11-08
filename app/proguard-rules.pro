# R8/ProGuard-Regeln der App

# ML Kit (Übersetzung): Die Komponenten-Registrare werden nur per Name aus dem Manifest geladen und würden sonst
# von R8 entfernt (Folge: NullPointerException in RemoteModelManager.getInstance() im Release-Build).
-keep class * implements com.google.firebase.components.ComponentRegistrar { *; }
-keep class com.google.mlkit.nl.translate.NaturalLanguageTranslateRegistrar { *; }
-keep class com.google.mlkit.common.internal.CommonComponentRegistrar { *; }
