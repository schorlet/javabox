package demo.gap.server.resource;

import java.text.ParseException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.sun.jersey.api.NotFoundException;

import demo.gap.shared.domain.pojo.Activity;
import demo.gap.shared.domain.pojo.Gap;
import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.GapService;
import demo.gap.shared.mem.MemDomainUtil;

/**
 * ActivityResource
 * 
 * GET activity/{id}
 * 
 * POST activity
 *      consumes: application/x-www-form-urlencoded
 *      
 * PUT activity/{id}
 *      consumes: application/x-www-form-urlencoded
 * 
 * DELETE activity/{id}
 */
@Path("activity")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ActivityResource {
    static final Logger logger = LoggerFactory.getLogger(ActivityResource.class);

    // @Context
    // UriInfo uriInfo;

    public ActivityResource() {}

    @GET
    @Path("{id:(\\d){6}}")
    public Response get(@PathParam("id") final String id) {
        logger.debug("get {}", id);

        final Activity activity = activityService.getById(id);
        if (activity == null) throw new NotFoundException("NotFound: " + id);

        return Response.ok(activity).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@FormParam("username") final String username,
        @FormParam("day") final String day, @FormParam("time") final String time,
        @FormParam("gap") final String gapid) throws ParseException {

        logger.debug("create {} {} {} {}", new Object[] { username, day, time, gapid });

        final Activity activity = updateActivity(null, username, day, time, gapid);
        return Response.ok(activity).build();
    }

    @PUT
    @Path("{id:(\\d){6}}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response update(@PathParam("id") final String id,
        @FormParam("username") final String username, @FormParam("day") final String day,
        @FormParam("time") final String time, @FormParam("gap") final String gapid)
        throws ParseException {

        logger.debug("update {} {} {} {} {}", new Object[] { id, username, day, time, gapid });

        final Activity activity = updateActivity(id, username, day, time, gapid);
        return Response.ok(activity).build();
    }

    @DELETE
    @Path("{id:(\\d){6}}")
    public Response delete(@PathParam("id") final String id) {
        logger.debug("delete {}", id);

        final Activity activity = activityService.getById(id);
        if (activity == null) throw new NotFoundException("NotFound: " + id);

        activityService.remove(activity);
        return Response.noContent().build();
    }

    /*
     * private
     */

    @Inject
    GapService gapService;

    @Inject
    ActivityService activityService;

    private Activity updateActivity(final String id, final String username, final String day,
        final String time, final String gapid) throws ParseException {

        final Activity activity = getActivity(id, gapid);

        activity.setDay(DateUtils.parseDate(day,
            new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() }));

        activity.setTime(time);
        activity.setUser(username);

        activityService.add(activity);
        return activity;
    }

    private Activity getActivity(final String id, final String gapid) {
        Activity activity = null;
        Gap gap = null;

        if (id != null) {
            activity = activityService.getById(id);

        } else {
            gap = gapService.getById(gapid);
            if (gap == null) throw new NotFoundException("Gap NotFound: " + gapid);
        }

        if (activity == null) {
            final String newId = id != null ? id : MemDomainUtil.randomActivityId();
            activity = new Activity(newId, gap);
        }

        return activity;
    }

}
