package world.tutorialspoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhengzebiao
 * @date 2020/2/12 23:28
 */
@Component
public class TextEditor {

    @Resource
    private SpellChecker spellChecker;

    public TextEditor() {
    }


    public TextEditor(SpellChecker spellChecker) {
        System.out.println("Inside TextEditor constuctor.");
        this.spellChecker = spellChecker;
    }

    public SpellChecker getSpellChecker() {
        return spellChecker;
    }


    public void setSpellChecker(SpellChecker spellChecker) {
        System.out.println("Inside TextEditor setting method.");
        this.spellChecker = spellChecker;
    }

    public void spellcheck() {
        spellChecker.checkSpelling();
    }

}