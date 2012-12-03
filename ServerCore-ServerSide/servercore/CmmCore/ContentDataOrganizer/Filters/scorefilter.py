'''
Created on Nov 17, 2012

this class filters out low scoring stuff

@author: hunlan
'''
from servercore.CmmCore.ContentDataOrganizer.Filters.filterinterface import FilterInterface
from servercore.CmmData.models import Media, Mood, Score, Deleted
from servercore import CmmData
from servercore.settings import IS_TEST_SERVER, DEPLOY

class ScoreFilter(FilterInterface):
    THRESHOLD = 60
    EMPTY_DB = -1
    NO_FINAL_SCORE = 0
    
    #@Override: Score Filter
    # return -1 on empty database
    #         0 on no final score yet
    #         1 on success
    @staticmethod
    def filter(mood, content, delete_ratio=None):
        # check param
        assert(content in Media.CONTENT_TYPES)
        assert(mood in Mood.MOOD_TYPES)
        
        # get content
        medias = Media.objects.filter(moods=mood, content_type=content)
        ScoreFilter._calculateFinalScore(medias)
        
        # get number to delete
        total_num = len(medias)
        delete_num = total_num
        if delete_ratio != None :
            delete_num = int(delete_ratio * total_num)
        
        # short circuit
        if len(medias) == 0:
            if IS_TEST_SERVER or DEPLOY:
                print 'empty media db with mood ' + mood + ' and content ' + content
            return ScoreFilter.EMPTY_DB
        
        # generating score map. TODO, use Media.objects.filter
        score_map = {}
        for m in medias :
            s = m.score
            if s.final_score != -1:
                score_map[m.id] = s.final_score
            # else:
            #    score_map[m.id] = s.initial_score

        # short circult
        if len(score_map) == 0:
            if IS_TEST_SERVER or DEPLOY:
                print 'no data has final_score yet'
            return ScoreFilter.NO_FINAL_SCORE
        
        #Filter
        num_deleted = 0
        min_score = ScoreFilter._getMinimum(score_map)
        while min_score[1] < ScoreFilter.THRESHOLD:
            if delete_num > 0:
                m = min_score[0]
                
                # delete
                Media.objects.get(id=m).move_to_trash()
                del score_map[m]
                CmmData.models.destory(m, content) #check if m.id?
                
                # get next min score
                delete_num -= 1
                num_deleted += 1
                
                if len(score_map) > 0 :
                    min_score = ScoreFilter._getMinimum(score_map)
                else:
                    break
            else:
                break
        
        return num_deleted
        
    # get minimum from m ( a list)
    @staticmethod
    def _getMinimum(m):
        for key, value in sorted(m.iteritems(), key=lambda (k,v): (v,k)):
            return (key, value)
            
        return None
    
    @staticmethod
    def _calculateFinalScore(medias):
        for m in medias:
            m.doFinalScoreIfOver()