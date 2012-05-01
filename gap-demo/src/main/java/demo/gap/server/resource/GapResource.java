package demo.gap.server.resource;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.view.Viewable;

import demo.gap.shared.domain.pojo.Activity;
import demo.gap.shared.domain.pojo.Gap;
import demo.gap.shared.domain.pojo.JerseyObject;
import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.Filter;
import demo.gap.shared.domain.service.GapService;
import demo.gap.shared.mem.MemDomainUtil;

/**
 * GapResource
 * 
 * GET /resource/gap/{id}
 * GET /resource/gap/{id}/html
 * GET /resource/gap/{id}/activities
 * 
 * DELETE /resource/gap/{id}
 * 
 * POST /resource/gap
 *      consumes: application/x-www-form-urlencoded
 *      
 * PUT /resource/gap/{id}
 *      consumes: application/x-www-form-urlencoded
 *      
 * PUT /resource/gap/{id}
 *      consumes: application/xml, application/json
 * 
 */
@Path("gap")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class GapResource {
    static final Logger logger = LoggerFactory.getLogger(GapResource.class);

    // @Context
    // UriInfo uriInfo;

    public GapResource() {}

    @GET
    @Path("{id:000\\w{5}}")
    public Response get(@PathParam("id") final String id) {
        logger.debug("get {}", id);

        final Gap gap = gapService.getById(id);
        if (gap == null) throw new NotFoundException("NotFound: " + id);

        return Response.ok(gap).build();
    }

    @GET
    @Path("{id:000\\w{5}}/html")
    @Produces(MediaType.TEXT_HTML)
    public Viewable getHtml(@PathParam("id") final String id) {
        logger.debug("getHtml {}", id);

        Gap gap = gapService.getById(id);
        if (gap == null) {
            gap = new Gap(id, null, null);
        }

        return new Viewable("/gap", gap);
    }

    @GET
    @Path("{id:000\\w{5}}/activities")
    public Response getActivities(@PathParam("id") final String id) {
        logger.debug("getActivities {}", id);

        final Gap gap = gapService.getById(id);
        if (gap == null) throw new NotFoundException("NotFound: " + id);

        final Set<Activity> activities = activityService.getByGapId(id);

        final Set<Gap> gaps = new HashSet<Gap>();
        gaps.add(gap);
        Filter.merge(gaps, activities);

        return Response.ok(new JerseyObject(gaps, activities)).build();
    }

    @DELETE
    @Path("{id:000\\w{5}}")
    public Response delete(@PathParam("id") final String id) {
        logger.debug("delete {}", id);

        final Gap gap = gapService.getById(id);
        if (gap == null) throw new NotFoundException("NotFound: " + id);

        gapService.remove(gap);
        return Response.noContent().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@FormParam("version") final String version,
        @FormParam("description") final String description) throws URISyntaxException {

        logger.debug("create {} {}", new Object[] { version, description });

        final Gap updateGap = updateGap(null, version, description);
        return Response.ok(updateGap).build();
    }

    @PUT
    @Path("{id:000\\w{5}}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response update(@PathParam("id") final String id,
        @FormParam("version") final String version,
        @FormParam("description") final String description) throws URISyntaxException {

        logger.debug("update {} {} {}", new Object[] { id, version, description });

        final Gap updateGap = updateGap(id, version, description);
        return Response.ok(updateGap).build();
    }

    @PUT
    @Path("{id:000\\w{5}}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response jsonUpdate(@PathParam("id") final String id, final Gap gap)
        throws URISyntaxException {

        logger.debug("jsonUpdate {}", gap);

        final Gap updateGap = updateGap(id, gap);
        return Response.ok(updateGap).build();
    }

    /*
     * private 
     */

    @Inject
    GapService gapService;

    @Inject
    ActivityService activityService;

    private Gap updateGap(final String id, final Gap gap) {
        return updateGap(id, gap.getVersion(), gap.getDescription());
    }

    private Gap updateGap(final String id, final String version, final String description) {
        final Gap gap = getGap(id);

        gap.setVersion(version);
        gap.setDescription(description);

        gapService.add(gap);
        return gap;
    }

    private Gap getGap(final String id) {
        Gap gap = null;

        if (id != null) {
            gap = gapService.getById(id);
        }

        if (gap == null) {
            final String newId = id != null ? id : MemDomainUtil.randomGapId();
            gap = new Gap(newId, null, null);
        }

        return gap;
    }
}
