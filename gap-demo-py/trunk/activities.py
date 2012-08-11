# -*- coding: utf-8 -*-
import web, json, logging
import model
from render import render_json


class ActivityResource:
    """activity resource"""

    def GET(self, activity_id):
        return self._render_activity(activity_id)

    def PUT(self, activity_id):
        try:
            data = web.data()
            logging.debug('data: %s', data)

            activity = json.loads(data)

            model.update_activity(activity_id, activity['time'])

        except Exception as e:
            logging.error(e, exc_info=1)
            web.ctx.status = '500 Server Error :%s' % e
        else:
            return self._render_activity(activity_id)

    def POST(self):
        try:
            data = web.data()
            logging.debug('data: %s', data)

            activity = json.loads(data)
            activity_id = model.random_activity_id()

            model.create_activity(activity_id, activity['day'], activity['time'],
                    activity['gap_id'], activity['user'])

        except Exception as e:
            logging.error(e, exc_info=1)
            web.ctx.status = '500 Server Error :%s' % e
        else:
            return self._render_activity(activity_id)

    def DELETE(self, activity_id):
        model.del_activity(activity_id)
        web.ctx.status = '204 No Content'

    def _render_activity(self, activity_id):
        try:
            activity = self._get_activity(activity_id)
            web.header('Content-Type', 'application/json; charset=UTF-8')
            return render_json(activity)
        except Exception as e:
            logging.error(e, exc_info=1)
            web.ctx.status = '404 Not Found'

    def _get_activity(self, activity_id):
        # get activity
        activities = model.read_activity(activity_id).list()
        # may throw exception if activities is empty
        activity = activities.pop()
        # set link
        setattr(activity, 'link', '/resource/activity/' + activity.id)
        return activity
