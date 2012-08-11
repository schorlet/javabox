# -*- coding: utf-8 -*-
import web, json, datetime


render_html = web.template.render('templates')


def render_json(*args, **kwargs):
    if args:
        return json.dumps(args[0], default=_render_json_handler)
    else:
        return json.dumps(kwargs, default=_render_json_handler)


def _render_json_handler(obj):
    if isinstance(obj, (datetime.date, datetime.datetime)):
        return obj.isoformat()
    raise TypeError('unsupported type: %s' % type(obj))
