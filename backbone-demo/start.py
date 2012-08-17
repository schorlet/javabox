#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web


urls = (
    '/', 'Index'
)

render_html = web.template.render('templates')

class Index:
    def GET(self):
        return render_html.index()


if __name__ == '__main__':
    app = web.application(urls, globals(), autoreload=False)
    app.run()
