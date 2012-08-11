# -*- coding: utf-8 -*-
import web, json, logging
import model
from render import render_json


class UsersResource:
    """users resource"""
    def GET(self):
        users = model.read_users()
        web.header('Content-Type', 'application/json; charset=UTF-8')
        return render_json(users=list(users))


class VersionsResource:
    """versions resource"""
    def GET(self):
        versions = model.read_versions()
        web.header('Content-Type', 'application/json; charset=UTF-8')
        return render_json(versions=list(versions))


class GapsResource:
    """gaps resource"""

    def GET(self):
        # get gaps
        gaps = model.read_all_gaps().list()
        # set link
        for gap in gaps:
            setattr(gap, 'link', '/resource/gap/' + gap.id)
        # return json
        web.header('Content-Type', 'application/json; charset=UTF-8')
        return render_json(gaps=gaps)

    def DELETE(self):
        model.reset()
        web.ctx.status = '204 No Content'


class GapResource:
    """gap resource"""

    def __init__(self):
        self.gap_form = web.form.Form(
            web.form.Textbox('version',
                        web.form.notnull,
                        web.form.regexp('\d\.\d\.\d', 'version must be three digit seperated by dot')),
            web.form.Textbox('description',
                        web.form.notnull,
                        web.form.regexp('.{1,30}', 'description must be {1,30} length'))
        )

    def GET(self, id):
        return self._render_gap(id)

    def PUT(self, id):
        content = web.ctx.environ["CONTENT_TYPE"]

        if content.startswith('application/json'):
            return self._put_json(id)
        elif content.startswith('application/x-www-form-urlencoded'):
            return self._put_form(id)
        else:
            web.ctx.status = '415 Unsupported Media Type'

    def POST(self):
        form = self.gap_form()
        if self._validate_form(form):
            try:
                id = model.random_gap_id()
                model.create_gap(id, form.version.value, form.description.value)
            except Exception as e:
                logging.error(e, exc_info=1)
                web.ctx.status = '400 Bad Request :%s' % e
            else:
                return self._render_gap(id)

    def DELETE(self, id):
        model.del_gap(id)
        web.ctx.status = '204 No Content'

    def _put_form(self, id):
        form = self.gap_form()
        if self._validate_form(form):
            try:
                model.update_gap(id, form.version.value, form.description.value)
            except Exception as e:
                logging.error(e, exc_info=1)
                web.ctx.status = '400 Bad Request :%s' % e
            else:
                return self._render_gap(id)

    def _put_json(self, id):
        try:
            data = web.data()
            logging.debug('data: %s', data)
            gap = json.loads(data)
            model.update_gap(id, gap['version'], gap['description'])
        except Exception as e:
            logging.error(e, exc_info=1)
            web.ctx.status = '400 Bad Request :%s' % e
        else:
            return self._render_gap(id)

    def _render_gap(self, id):
        try:
            gap = self._get_gap(id)
            web.header('Content-Type', 'application/json; charset=UTF-8')
            return render_json(gap)
        except Exception as e:
            logging.error(e, exc_info=1)
            web.ctx.status = '404 Not Found'

    def _get_gap(self, id):
        # get gap
        gaps = model.read_gap(id).list()
        # may throw exception if gaps is empty
        gap = gaps.pop()
        # set link
        setattr(gap, 'link', '/resource/gap/' + gap.id)
        return gap

    def _validate_form(self, form):
        if not form.validates():
            message = ''
            for input in form.inputs:
                if input.note:
                    message += input.name, ": ", input.note
            web.ctx.status = '400 Bad Request: %s' % message
            return False
        return True
