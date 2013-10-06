package eu.artofcoding.redisync.web;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import java.util.logging.Logger;

public class MyPhaseListener implements PhaseListener {

    private static final Logger logger = Logger.getLogger("global");

    private static final long serialVersionUID = 3131268230269004403L;

    public MyPhaseListener() {
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        logger.info(String.format("After - %s", event.getPhaseId().toString()));
        if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
            logger.info("Done with request!\n");
        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        if (event.getPhaseId() == PhaseId.RESTORE_VIEW) {
            logger.info("Processing new request!");
        }
        logger.info(String.format("Before - %s", event.getPhaseId().toString()));
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

}
