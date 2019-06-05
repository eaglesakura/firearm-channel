apply(from = "../dsl/android-library.gradle")
apply(from = "../dsl/ktlint.gradle")
apply(from = "../dsl/bintray.gradle")

dependencies {
    "api"("androidx.annotation:annotation:1.0.2")
    "api"("androidx.appcompat:appcompat:1.0.2")
    "implementation"("androidx.lifecycle:lifecycle-extensions:2.0.0")
    "implementation"("androidx.lifecycle:lifecycle-viewmodel-ktx:2.0.0")
}