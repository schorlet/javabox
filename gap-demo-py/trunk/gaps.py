# -*- coding: utf-8 -*-
import web, json, logging
import model
from render import render_json

class GapsResource:
    """gaps resource"""

    def GET(self, version, user, from_day, to_day):
        if not version:
            # get gaps
            gaps = model.read_all_gaps().list()
        else:
            version = version[1:]
            # get gaps
            gaps = model.read_gaps(version).list()

        if user:
            # get activities
            user = user[1:]
            if from_day: from_day = from_day[1:]
            if to_day: to_day = to_day[1:]
            activities = model.read_activities(version, user, from_day, to_day).list()

            # set activity link
            for activity in activities:
                setattr(activity, 'link', '/resource/activity/' + activity.id)

            # merge gaps and activities
            model.merge(gaps, activities)

        # set link
        for gap in gaps:
            setattr(gap, 'link', '/resource/gap/' + gap.id)

        # return json
        web.header('Content-Type', 'application/json; charset=UTF-8')
        return render_json(gaps=gaps)

    def DELETE(self, version, user, from_day, to_day):
        model.reset()
        web.ctx.status = '204 No Content'


class GapResource:
    """gap resource"""

    def GET(self, gap_id):
        return self._render_gap(gap_id)

    def PUT(self, gap_id):
        try:
            data = web.data()
            logging.debug('data: %s', data)
            gap = json.loads(data)
            model.update_gap(gap_id, gap['version'], gap['description'])
        except Exception as e:
            logging.error(e, exc_info=1)
            web.ctx.status = '500 Server Error :%s' % e
        else:
            return self._render_gap(gap_id)

    def POST(self):
        try:
            data = web.data()
            logging.debug('data: %s', data)
            gap = json.loads(data)
            gap_id = model.random_gap_id()
            logging.debug('new gap_id: %s', gap_id)
            model.create_gap(gap_id, gap['version'], gap['description'])
        except Exception as e:
            logging.error(e, exc_info=1)
            web.ctx.status = '500 Server Error :%s' % e
        else:
            return self._render_gap(gap_id)

    def DELETE(self, gap_id):
        model.del_gap(gap_id)
        web.ctx.status = '204 No Content'

    def _render_gap(self, gap_id):
        try:
            gap = self._get_gap(gap_id)
            web.header('Content-Type', 'application/json; charset=UTF-8')
            return render_json(gap)
        except Exception as e:
            logging.error(e, exc_info=1)
            web.ctx.status = '404 Not Found'

    def _get_gap(self, gap_id):
        # get gap
        gaps = model.read_gap(gap_id).list()
        # may throw exception if gaps is empty
        gap = gaps.pop()
        # set link
        setattr(gap, 'link', '/resource/gap/' + gap.id)
        return gap
