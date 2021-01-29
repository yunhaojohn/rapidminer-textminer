package hanMiner.operator.analyzing;

import com.hankcs.hanlp.seg.common.Term;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import hanMiner.text.SimpleDocumentSet;

import java.util.ArrayList;
import java.util.List;

import static hanMiner.operator.processing.Tokenize.tokenize;

/**
 * This operator recognize Chinese name/place/organization entities in text. Only the entities will be
 * kept in the output.
 *
 * @author joeyhaohao
 *
 */
public class EntityRecognition extends Operator {
    private static final String PARAMETER_RECOGNIZE = "recognize";
    private static final String[] ENTITIES = { "name", "place", "organization"};
    private static final int ENTITY_NAME = 0;
    private static final int ENTITY_PLACE = 1;
    private static final int ENTITY_ORGANIZATION = 2;
    private static final String ENTITY_TAG_NAME = "nr";
    private static final String ENTITY_TAG_PLACE = "ns";
    private static final String ENTITY_TAG_ORGANIZATION = "nt";

    private InputPort documentSetInput = getInputPorts().createPort("document set");
    private OutputPort documentSetOutput = getOutputPorts().createPort("document set");

    public EntityRecognition(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = new ParameterTypeCategory(PARAMETER_RECOGNIZE,
                "Entity type to recognize",
                ENTITIES,
                ENTITY_NAME,
                false);
        types.add(type);

        return types;
    }

    @Override
    public void doWork() throws OperatorException {
        SimpleDocumentSet documentSet = documentSetInput.getData(SimpleDocumentSet.class);
        List<String> output = new ArrayList<>();
        String entity_tag = null;
        List<List<Term>> termsList = null;
        switch (getParameterAsInt(PARAMETER_RECOGNIZE)) {
            case ENTITY_NAME:
                entity_tag = ENTITY_TAG_NAME;
                termsList = tokenize(documentSet);
                break;
            case ENTITY_PLACE:
                entity_tag = ENTITY_TAG_PLACE;
                termsList = tokenize(documentSet, true, false);
                break;
            case ENTITY_ORGANIZATION:
                entity_tag = ENTITY_TAG_ORGANIZATION;
                termsList = tokenize(documentSet, false, true);
                break;
        }

        for (List<Term> terms: termsList){
            List<String> entities = new ArrayList<>();
            for (Term term: terms) {
                if (term.nature.toString().contains(entity_tag)) {
                    entities.add(term.word);
                }
            }
            output.add(String.join(" ", entities));
        }

        SimpleDocumentSet resultObject = new SimpleDocumentSet(output);
        documentSetOutput.deliver(resultObject);
    }
}
