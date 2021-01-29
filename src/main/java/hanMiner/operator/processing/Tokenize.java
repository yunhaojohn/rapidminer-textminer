package hanMiner.operator.processing;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import hanMiner.text.SimpleDocumentSet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This operator segments Chinese documents into words (tokens).
 *
 * @author joeyhaohao
 *
 */
public class Tokenize extends Operator {

    private InputPort documentSetInput = getInputPorts().createPort("document set");
    private OutputPort documentSetOutput = getOutputPorts().createPort("document set");

    public Tokenize(OperatorDescription description) {
        super(description);
    }

    public static List<List<Term>> tokenize(SimpleDocumentSet documentSet, boolean enable_place,
                                            boolean enable_institution) {
        List<List<Term>> output = new ArrayList<>();
        // Name entity recognition is enabled by default
        Segment segment = HanLP.newSegment();
        if (enable_place) {
            segment.enablePlaceRecognize(true);
        }
        if (enable_institution) {
            segment.enableOrganizationRecognize(true);
        }
        for (String doc: documentSet.getDocuments()){
            List<Term> segments = segment.seg(doc);
            output.add(segments);
        }
        return output;
    }

    public static List<List<Term>> tokenize(SimpleDocumentSet documentSet) {
        return tokenize(documentSet, false, false);
    }

    @Override
    public void doWork() throws OperatorException {
        SimpleDocumentSet documentSet = documentSetInput.getData(SimpleDocumentSet.class);
        List<List<Term>> termsList = tokenize(documentSet);
        List<String> output = new ArrayList<>();
        for (List<Term> terms: termsList){
            List<String> words = terms.stream().map(term -> term.word).collect(Collectors.toList());
            output.add(String.join(" ", words));
        }

        SimpleDocumentSet resultObject = new SimpleDocumentSet(output);
        documentSetOutput.deliver(resultObject);
    }
}
