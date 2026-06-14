package app.feature.faq

data class FaqUiState(
    val questions: List<FaqQuestionUiModel> = defaultFaqQuestions,
    val initialExpandedQuestionId: String? = null,
)

data class FaqQuestionUiModel(
    val id: String,
    val question: String,
    val answer: String,
    val icon: FaqQuestionIcon,
)

enum class FaqQuestionIcon {
    Wind,
    Co2,
    Operator,
    Participation,
}

private val defaultFaqQuestions = listOf(
    FaqQuestionUiModel(
        id = "wind-turbine-function",
        question = "Wie funktioniert eine Windkraftanlage?",
        answer = "Windkraftanlagen wandeln die kinetische Energie des Windes in elektrische Energie um. Der Wind dreht die Rotorblätter, die wiederum einen Generator antreiben, der Strom erzeugt.",
        icon = FaqQuestionIcon.Wind,
    ),
    FaqQuestionUiModel(
        id = "co2-savings",
        question = "Wie viel CO₂ spart Windenergie?",
        answer = "Die Einsparung hängt vom Standort und der Jahresproduktion ab. Grundsätzlich ersetzt Windstrom fossile Stromerzeugung und vermeidet dadurch einen großen Teil der damit verbundenen CO₂-Emissionen.",
        icon = FaqQuestionIcon.Co2,
    ),
    FaqQuestionUiModel(
        id = "operators",
        question = "Wer betreibt Windparks in Deutschland?",
        answer = "Windparks werden von Energieunternehmen, Stadtwerken, Projektgesellschaften, Genossenschaften und teils auch von Bürgerenergie-Initiativen betrieben.",
        icon = FaqQuestionIcon.Operator,
    ),
    FaqQuestionUiModel(
        id = "participation",
        question = "Kann ich als Bürger teilnehmen?",
        answer = "Ja, je nach Projekt sind Beteiligungen über Bürgerenergie-Modelle, Genossenschaften oder kommunale Angebote möglich. Die konkreten Optionen unterscheiden sich regional.",
        icon = FaqQuestionIcon.Participation,
    ),
)
