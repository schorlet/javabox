#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""gaps demo using webpy 0.3"""

import web, logging
import gaps, activities, users, versions
from render import render_html


# Url mappings
urls = (
    '/', 'Index',

    # GET
    '/resource/users', 'users.UsersResource',
    # GET
    '/resource/versions', 'versions.VersionsResource',

    # GET, DELETE /version/user/from/to
    '/resource/gaps(/\\d\\.\\d\\.\\d)?(/\\w{4})?(/-?\\d+)?(/-?\\d+)?', 'gaps.GapsResource',

    # POST
    '/resource/gap', 'gaps.GapResource',
    # GET, PUT, DELETE /gap_id
    '/resource/gap/(000\\w{5})', 'gaps.GapResource',
    '/resource/gaps/(000\\w{5})', 'gaps.GapResource',

    # POST
    '/resource/activity', 'activities.ActivityResource',
    # GET, PUT, DELETE /activity_id
    '/resource/activity/(\\d{6})', 'activities.ActivityResource'
)


class Index:
    def GET(self):
        return render_html.index()


if __name__ == '__main__':
    FORMAT = "%(asctime)s %(levelname)s %(module)s:%(name)s - %(message)s"
    logging.basicConfig(format=FORMAT, datefmt='%H:%M:%S', level=logging.DEBUG)

    app = web.application(urls, globals(), autoreload=True)
    app.internalerror = web.debugerror
    app.run()
