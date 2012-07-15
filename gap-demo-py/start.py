#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""gaps demo using webpy 0.3"""

import web, logging
import gaps, activities
from render import render_html


# Url mappings
urls = (
    # html pages
    '/', 'Index',
    '/gaps.html', 'Gaps',
    '/activities.html', 'Activities',

    # rest services
    '/resource/gap', 'gaps.GapResource', # POST (create)
    '/resource/gap/(000\\w{5})', 'gaps.GapResource', # GET, PUT, DELETE (by gap id)

    '/resource/gaps', 'gaps.GapsResource', # GET, DELETE (all)
    '/resource/gaps/users', 'gaps.UsersResource', # GET
    '/resource/gaps/versions', 'gaps.VersionsResource', # GET
    '/resource/gaps/(\\w{4})', 'activities.ActivitiesResource', # GET (by user)

    '/resource/activity', 'activities.ActivityResource', # POST (create)
    '/resource/activity/(\\d{6})', 'activities.ActivityResource' # GET, PUT, DELETE (by activity id)
)


class Index:
    """index.html"""
    def GET(self):
        return render_html.index()


class Gaps:
    """gaps.html"""
    def GET(self):
        return render_html.gaps()


class Activities:
    """activities.html"""
    def GET(self):
        return render_html.activities()


if __name__ == '__main__':
    FORMAT = "%(asctime)s %(levelname)s %(module)s:%(name)s - %(message)s"
    logging.basicConfig(format=FORMAT, datefmt='%H:%M:%S', level=logging.DEBUG)

    app = web.application(urls, globals(), autoreload=True)
    app.internalerror = web.debugerror
    app.run()
