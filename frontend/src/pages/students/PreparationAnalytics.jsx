import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import axios from 'axios';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { BarChart3, ArrowLeft, Loader2, CheckCircle2, Clock, TrendingUp } from 'lucide-react';
import { Button } from '../../components/ui/button';

const PreparationAnalytics = () => {
  const { roleName } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [preparation, setPreparation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!user?.id || !roleName) return;

    const fetchPreparation = async () => {
      try {
        const decodedRoleName = decodeURIComponent(roleName || '');
        const response = await axios.get(
          `http://localhost:8080/api/role-preparation/${encodeURIComponent(decodedRoleName)}?studentId=${user.id}`
        );
        if (response.data) {
          setPreparation(response.data);
        } else {
          setError('No preparation found for this role');
        }
      } catch (err) {
        console.error('Error fetching preparation:', err);
        setError('Failed to load preparation data');
      } finally {
        setLoading(false);
      }
    };

    fetchPreparation();
  }, [user?.id, roleName]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
      </div>
    );
  }

  if (error || !preparation) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <Card className="w-full max-w-md">
          <CardContent className="pt-6">
            <p className="text-center text-gray-600">{error || 'No preparation data found'}</p>
            <Button onClick={() => navigate(-1)} className="mt-4 w-full">
              <ArrowLeft className="mr-2 h-4 w-4" />
              Go Back
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  const skillProgress = preparation.skillProgress || {};
  const totalSkills = Object.keys(skillProgress).length;
  const completedSkills = Object.values(skillProgress).filter(p => p?.completed).length;
  const completionPercentage = totalSkills > 0 ? (completedSkills / totalSkills) * 100 : 0;

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-6">
          <Button variant="ghost" onClick={() => navigate(-1)}>
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back
          </Button>
        </div>

        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Preparation Analytics: {decodeURIComponent(roleName)}
          </h1>
          <p className="text-gray-600">
            Track your progress and skill development
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <TrendingUp className="h-5 w-5 text-blue-600" />
                Overall Progress
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-blue-600">
                {completionPercentage.toFixed(1)}%
              </div>
              <p className="text-sm text-gray-600 mt-2">
                {completedSkills} of {totalSkills} skills completed
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <CheckCircle2 className="h-5 w-5 text-green-600" />
                Completed Skills
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-green-600">
                {completedSkills}
              </div>
              <p className="text-sm text-gray-600 mt-2">
                Skills mastered
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Clock className="h-5 w-5 text-orange-600" />
                Remaining Skills
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-orange-600">
                {totalSkills - completedSkills}
              </div>
              <p className="text-sm text-gray-600 mt-2">
                Skills to complete
              </p>
            </CardContent>
          </Card>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Skill Progress Details</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {Object.entries(skillProgress).map(([skillName, progress]) => (
                <div key={skillName} className="border rounded-lg p-4">
                  <div className="flex items-center justify-between mb-2">
                    <h3 className="font-semibold text-gray-900">{skillName}</h3>
                    {progress?.completed ? (
                      <span className="flex items-center gap-1 text-green-600">
                        <CheckCircle2 className="h-4 w-4" />
                        Completed
                      </span>
                    ) : (
                      <span className="text-gray-500">In Progress</span>
                    )}
                  </div>
                  {progress?.score && (
                    <p className="text-sm text-gray-600">
                      Test Score: {progress.score}
                    </p>
                  )}
                  {progress?.completedDate && (
                    <p className="text-sm text-gray-600">
                      Completed: {new Date(progress.completedDate).toLocaleDateString()}
                    </p>
                  )}
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default PreparationAnalytics;

