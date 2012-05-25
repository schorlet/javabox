package demo.gap.server.resource;

import java.util.Date;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sun.jersey.api.view.Viewable;

import demo.gap.shared.domain.pojo.Activity;
import demo.gap.shared.domain.pojo.Gap;
import demo.gap.shared.domain.pojo.JerseyObject;
import demo.gap.shared.domain.pojo.JstlObject;
import demo.gap.shared.domain.pojo.User;
import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.Filter;
import demo.gap.shared.domain.service.GapService;
import demo.gap.shared.domain.service.UserService;
import demo.gap.shared.mem.MemDomainUtil;
import demo.gap.shared.mem.SampleData;

/**
 * GapsResource
 * 
 * GET /resource/gaps/{id}
 * GET /resource/gaps
 * DELETE /resource/gaps
 * 
 * GET /resource/gaps/users
 * GET /resource/gaps/versions
 * 
 * GET /resource/gaps/{username}
 * GET /resource/gaps/{username}/html
 * 
 */
@Path("gaps")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class GapsResource {
    static final Logger logger = LoggerFactory.getLogger(GapsResource.class);

    // @Context
    // UriInfo uriInfo;

    public GapsResource() {}

    @Path("{id:000\\w{5}}")
    public GapResource getGapResource(@PathParam("id") final String id) {
        logger.debug("getById {}", id);
        return gapResource.get();
    }

    @GET
    public Response get() {
        logger.debug("get");
        final Set<Gap> gaps = gapService.getGaps();
        return Response.ok(new JerseyObject(gaps)).build();
    }

    @DELETE
    public Response delete() {
        logger.debug("delete");
        SampleData.reset(gapService, activityService, userService);
        return Response.noContent().build();
    }

    /*
     * 
     */

    @GET
    @Path("users")
    public Response getUsers() {
        logger.debug("getUsers");
        final Set<User> users = userService.getUsers();
        return Response.ok(new JerseyObject().setUsers(users)).build();
    }

    @GET
    @Path("versions")
    public Response getVersions() {
        logger.debug("getVersions");
        final Set<String> versions = gapService.getVersions();
        return Response.ok(new JerseyObject().setVersions(versions)).build();
    }

    /*
     * 
     */

    @GET
    @Path("{username:\\w{4}}")
    public Response getByUser(@PathParam("username") final String username,
        @QueryParam("from") final Integer from, @QueryParam("to") final Integer to,
        @QueryParam("version") final String version) {

        logger.debug("getByUser {} {} {} {}", new Object[] { username, from, to, version });

        final Set<Gap> gaps = getGaps(version);
        final Set<Activity> activities = getActivities(username, from, to, version);
        Filter.merge(gaps, activities);

        return Response.ok(new JerseyObject(gaps, activities)).build();
    }

    @GET
    @Path("{username:\\w{4}}/html")
    @Produces(MediaType.TEXT_HTML)
    public Viewable getByUserHtml(@PathParam("username") final String username,
        @QueryParam("from") final Integer from, @QueryParam("to") final Integer to,
        @QueryParam("version") final String version) {

        logger.debug("getByUserHtml {} {} {} {}", new Object[] { username, from, to, version });

        final Set<Gap> gaps = getGaps(version);
        final Set<Activity> activities = getActivities(username, from, to, version);
        Filter.merge(gaps, activities);

        return new Viewable("/activities", new JstlObject(gaps, from, to));
    }

    /*
     * private
     */

    @Inject
    Provider<GapResource> gapResource;

    @Inject
    GapService gapService;

    @Inject
    ActivityService activityService;

    @Inject
    UserService userService;

    private Set<Activity> getActivities(final String username, final Integer from,
        final Integer to, final String version) {
        final Filter filter = new Filter().byUser(username);

        if (from != null && to != null) {
            final Date fromDay = MemDomainUtil.selectDay(from);
            final Date toDay = MemDomainUtil.selectDay(to);
            filter.byDayInterval(fromDay, toDay);
        }

        if (version != null) {
            filter.byVersion(version);
        }

        final Set<Activity> activities = activityService.getByFilter(filter);
        return activities;
    }

    private Set<Gap> getGaps(final String version) {
        final Filter filter = new Filter().byVersion(version);
        return gapService.getByFilter(filter);
    }
}
