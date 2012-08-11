# -*- coding: utf-8 -*-
import web
import model
from render import render_json


class VersionsResource:
    """versions resource"""
    def GET(self):
        versions = model.read_versions()
        web.header('Content-Type', 'application/json; charset=UTF-8')
        return render_json(versions=list(versions))
