# -*- coding: utf-8 -*-
import web, logging
import model
from render import render_json


class ActivitiesResource:
    """activities resource"""

    def GET(self, user):
        """get by user"""
        try:
            data = web.input()
            version = data.get('version')
            from_day = data.get('from')
            to_day = data.get('to')

            # get gaps
            gaps = model.read_gaps(version).list()
            # get activities
            activities = model.read_activities(user, from_day, to_day, version).list()
            # merge gaps and activities
            model.merge(gaps, activities)

            # set gap link
            for gap in gaps:
                setattr(gap, 'link', '/resource/gap/' + gap.id)

            # set activity link
            for activity in activities:
                setattr(activity, 'link', '/resource/activity/' + activity.id)

            web.header('Content-Type', 'application/json; charset=UTF-8')
            return render_json(gaps=gaps, activities=activities)

        except Exception as e:
            logging.error(e, exc_info=1)
            web.ctx.status = '500 Server error :%s' % e


class ActivityResource:
    """activity resource"""

    def GET(self, id):
        return self._render_activity(id)

    def PUT(self, id):
        try:
            data = web.input()
            logging.debug('data: %s', data)
            model.update_activity(id, data['time'])
        except Exception as e:
            logging.error(e, exc_info=1)
            web.ctx.status = '400 Bad Request :%s' % e
        else:
            return self._render_activity(id)

    def POST(self):
        try:
            data = web.input()
            logging.debug('data: %s', data)

            id = model.random_activity_id()
            model.create_activity(id, data['day'], data['time'],
                    data['gap'], data['username'])
        except Exception as e:
            logging.error(e, exc_info=1)
            web.ctx.status = '400 Bad Request :%s' % e
        else:
            return self._render_activity(id)

    def DELETE(self, id):
        model.del_activity(id)
        web.ctx.status = '204 No Content'

    def _render_activity(self, id):
        try:
            activity = self._get_activity(id)
            web.header('Content-Type', 'application/json; charset=UTF-8')
            return render_json(activity)
        except Exception as e:
            logging.error(e, exc_info=1)
            web.ctx.status = '404 Not Found'

    def _get_activity(self, id):
        # get activity
        activities = model.read_activity(id).list()
        # may throw exception if activities is empty
        activity = activities.pop()
        # set link
        setattr(activity, 'link', '/resource/activity/' + activity.id)
        return activity
