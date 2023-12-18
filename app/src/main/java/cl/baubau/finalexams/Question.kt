package cl.baubau.finalexams

class Question(val question: String, val answer: String, val options: List<String>)
{
    fun isCorrect(answer: String) : Boolean
    {
        return this.answer == answer;
    }
}
