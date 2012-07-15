# -*- coding: utf-8 -*-
import web, string, random
from datetime import date, timedelta

db = web.database(dbn='sqlite', db='gaps.sqlite')


def reset():
    pass


def read_users():
    return db.select('users', order='user')


def read_versions():
    return db.query('select distinct version from gaps order by version')


### gaps
def read_all_gaps():
    return db.where('gaps', order='id')


def read_gaps(version):
    if version is None:
        return db.where('gaps', order='version')
    else:
        return db.where('gaps', order='version', version=version)


### gap
def create_gap(id, version, description):
    db.insert('gaps', id=id, version=version, description=description)


def read_gap(id):
    return db.where('gaps', limit=1, id=id)


def update_gap(id, version, description):
    db.update('gaps', where='id = $id', vars={'id': id}, version=version, description=description)


def del_gap(id):
    db.delete('gaps', where="id=$id", vars=locals())


def random_gap_id():
    return ''.join(random.sample(set(string.ascii_uppercase + string.digits), 5)).rjust(8, '0')


### activities
def read_all_activities():
    return db.where('activities', order='id')


def read_activities(user, from_day, to_day, version):
    where = 'gaps.id = activities.gap_id'

    args = {'user': user}
    where += ' and activities.user = $user'

    if from_day:
        args['from_day'] = date.today() + timedelta(days=int(from_day))
        where += ' and activities.day >= $from_day'
    if to_day:
        args['to_day'] = date.today() + timedelta(days=int(to_day))
        where += ' and activities.day <= $to_day'
    if version:
        args['version'] = version
        where += ' and gaps.version = $version'

    return db.select(['gaps', 'activities'], what='activities.*, gaps.version',
        where=where, order='activities.day desc', vars=args)


def merge(gaps, activities):
    for gap in gaps:
        setattr(gap, 'activities', [])
        for activity in activities:
            if gap.id == activity.gap_id:
                gap.activities.append(activity.id)


### activity
def create_activity(id, day, time, gap_id, user):
    db.insert('activities', id=id, day=day, time=time, gap_id=gap_id, user=user)


def read_activity(id):
    return db.where('activities', limit=1, id=id)


def update_activity(id, time):
    db.update('activities', where='id = $id', vars={'id': id}, time=time)


def del_activity(id):
    db.delete('activities', where="id=$id", vars=locals())


def random_activity_id():
    return ''.join(random.sample(set(string.digits), 6))
