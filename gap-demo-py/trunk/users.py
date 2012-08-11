# -*- coding: utf-8 -*-
import web
import model
from render import render_json


class UsersResource:
    """users resource"""
    def GET(self):
        users = model.read_users()
        web.header('Content-Type', 'application/json; charset=UTF-8')
        return render_json(users=list(users))
